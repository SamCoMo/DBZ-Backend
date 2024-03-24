package com.samcomo.dbz.notification.service;

import static com.samcomo.dbz.global.exception.ErrorCode.FIREBASE_ACCESS_TOKEN_ERROR;
import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.global.exception.ErrorCode.PIN_NOTIFICATION_FAILED;
import static com.samcomo.dbz.global.exception.ErrorCode.REPORT_NOTIFICATION_FAILED;
import static com.samcomo.dbz.notification.model.constants.NotificationType.CHAT;
import static com.samcomo.dbz.notification.model.constants.NotificationType.PIN;
import static com.samcomo.dbz.notification.model.constants.NotificationType.REPORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.samcomo.dbz.chat.model.dto.ChatMessageDto;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import com.samcomo.dbz.notification.exception.NotiException;
import com.samcomo.dbz.notification.model.constants.NotificationType;
import com.samcomo.dbz.notification.model.dto.FcmMessageDto;
import com.samcomo.dbz.notification.model.dto.NotificationDto;
import com.samcomo.dbz.notification.model.dto.SendChatDto;
import com.samcomo.dbz.notification.model.dto.SendPinDto;
import com.samcomo.dbz.notification.model.dto.SendReportDto;
import com.samcomo.dbz.notification.model.dto.SendSingleDto;
import com.samcomo.dbz.notification.model.entity.Notification;
import com.samcomo.dbz.notification.model.repository.NotifiRepository;
import com.samcomo.dbz.report.model.dto.ReportDto;
import com.samcomo.dbz.report.model.dto.ReportDto.Form;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  @Value("${fcm.key.path}")
  private String SERVICE_ACCOUNT_JSON;
  @Value("${fcm.api.url}")
  private String FCM_API_URL;

  private final MemberRepository memberRepository;
  private final NotifiRepository notifiRepository;

  private final static int MAX_DISTANCE = 3;
  private final static String FIREBASE_AUTH_PREFIX = "Bearer ";
  private final static String CHAT_ROOM_ID_SPLITTER = "_";

  @Override
  @Transactional
  public void sendPinNotification(Long memberId) {

    Member member = validateOrGetMember(memberId);
    String token = member.getFcmToken();

    SendPinDto sendPinDto = SendPinDto.from(token);
    SendSingleDto sendSingleDto = SendSingleDto.from(sendPinDto);

    try {
      String message = makeSingleMessage(sendSingleDto);

      OkHttpClient client = new OkHttpClient();
      Request httpRequest = getHttpRequest(message);
      Response response = client.newCall(httpRequest).execute();

      log.info("핀 알림 전송 성공: {}", Objects.requireNonNull(response.body()).string());
    } catch (IOException e) {
      throw new NotiException(PIN_NOTIFICATION_FAILED);
    }
    getNotificationEntity(memberId, sendPinDto.getBody(), PIN);
  }

  @Override
  @Transactional
  public void sendReportNotification(ReportDto.Form reportForm) {

    List<Member> memberList = getActiveMemberNearBy(reportForm);
    List<String> fcmTokenList = getFcmTokenList(memberList);

    SendReportDto sendReportDto = SendReportDto.of(reportForm.getDescriptions());

    MulticastMessage message = makeMultipleMessage(sendReportDto, fcmTokenList);

    try {
      BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

      log.info("게시글 알림 성공 successCount: " + response.getSuccessCount());
    } catch (FirebaseMessagingException e) {
      log.error("알림 전송 실패 : " + e.getMessage());
      throw new NotiException(REPORT_NOTIFICATION_FAILED);
    }

    List<Notification> notificationList = new ArrayList<>();
    for (Member member : memberList) {
      notificationList.add(
          getNotificationEntity(member.getId(), sendReportDto.getBody(), REPORT)
      );
    }
    notifiRepository.saveAll(notificationList);
  }

  @Override
  @Transactional
  public void sendChatNotification(
      String chatRoomId, String senderId, ChatMessageDto.Request request) {

    Long recipientId = Long.valueOf(getRecipientId(chatRoomId, senderId));
    Member member = validateOrGetMember(recipientId);

    SendChatDto sendChatDto = SendChatDto.of(
        member.getNickname(), request.getContent(), member.getFcmToken());

    SendSingleDto sendSingleDto = SendSingleDto.from(sendChatDto);

    try {
      String message = makeSingleMessage(sendSingleDto);

      OkHttpClient client = new OkHttpClient();
      Request httpRequest = getHttpRequest(message);
      Response response = client.newCall(httpRequest).execute();

      log.info(" 채팅 알림 전송 성공: {}", Objects.requireNonNull(response.body()).string());
    } catch (IOException e) {
      throw new NotiException(PIN_NOTIFICATION_FAILED);
    }
    notifiRepository.save(getNotificationEntity(recipientId, sendChatDto.getBody(), CHAT));
  }

  private Member validateOrGetMember(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
  }

  @NotNull
  private static List<String> getFcmTokenList(List<Member> memberList) {
    return memberList.stream().map(Member::getFcmToken).collect(Collectors.toList());
  }

  private List<Member> getActiveMemberNearBy(Form reportForm) {
    return memberRepository.findAllInActive(
        reportForm.getLatitude(), reportForm.getLongitude(), MAX_DISTANCE);
  }

  private Notification getNotificationEntity(Long memberId, String message, NotificationType type) {
    return Notification.builder()
        .memberId(String.valueOf(memberId))
        .type(type)
        .message(message)
        .createdAt(LocalDateTime.now())
        .build();
  }

  private static String getRecipientId(String chatRoomId, String realSenderId) {
    // chatRoomId 는 "senderId_recipientId" or "recipientId_senderId"
    String[] memberIdList = chatRoomId.split(CHAT_ROOM_ID_SPLITTER);
    return isRecipientId(memberIdList[0], realSenderId) ? memberIdList[0] : memberIdList[1];
  }

  private static boolean isRecipientId(String firstMemberId, String realSenderId) {
    return !firstMemberId.equals(realSenderId);
  }

  @NotNull
  private Request getHttpRequest(String message) throws IOException {
    RequestBody requestBody = RequestBody.create(message,
        MediaType.get(APPLICATION_JSON_UTF8_VALUE));

    return new Request.Builder()
        .url(FCM_API_URL)
        .post(requestBody)
        .addHeader(AUTHORIZATION, FIREBASE_AUTH_PREFIX + getFirebaseAccessToken())
        .addHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
        .build();
  }

  @Override
  public List<NotificationDto> getNotificationList(String memberId) {
    validateOrGetMember(Long.parseLong(memberId));

    return notifiRepository.findAllByMemberId(memberId).stream()
        .map(NotificationDto::fromEntity)
        .collect(Collectors.toList());
  }

  private String getFirebaseAccessToken() {
    try {
      GoogleCredentials googleCredentials = GoogleCredentials
          .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
          .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

      googleCredentials.refreshIfExpired();
      return googleCredentials.refreshAccessToken().getTokenValue();

    } catch (IOException e) {
      throw new NotiException(FIREBASE_ACCESS_TOKEN_ERROR);
    }
  }

  private String makeSingleMessage(SendSingleDto send) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
        .message(FcmMessageDto.Message.builder()
            .token(send.getToken())
            .notification(FcmMessageDto.Notification.builder()
                .title(send.getTitle())
                .body(send.getBody())
                .build()
            ).build()).build();

    return objectMapper.writeValueAsString(fcmMessageDto);
  }

  private static MulticastMessage makeMultipleMessage(
      SendReportDto request, List<String> tokenList) {

    MulticastMessage message = MulticastMessage.builder()
        .setNotification(com.google.firebase.messaging.Notification.builder()
            .setTitle(request.getTitle())
            .setBody(request.getBody())
            .build())
        .addAllTokens(tokenList)
        .build();

    log.info("message: {}", request.getBody());
    return message;
  }
}
