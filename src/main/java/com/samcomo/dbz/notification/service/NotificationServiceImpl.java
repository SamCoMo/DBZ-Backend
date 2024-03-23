package com.samcomo.dbz.notification.service;

import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.global.exception.ErrorCode;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
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

  private final static int DISTANCE = 3;

  @Override
  @Transactional
  public void sendPinNotification(Long memberId) {

    try {

      Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
      String token = member.getFcmToken();

      SendPinDto sendPinDto = new SendPinDto(token);
      SendSingleDto sendsingleDto = SendSingleDto.builder()
          .body(sendPinDto.getBody())
          .title(sendPinDto.getTitle())
          .token(sendPinDto.getToken())
          .build();

      String message = makeSingleMessage(sendsingleDto);

      OkHttpClient client = new OkHttpClient();

      RequestBody requestBody = RequestBody.create(message,
          MediaType.get("application/json; charset=utf-8"));
      Request httpRequest = new Request.Builder()
          .url(FCM_API_URL)
          .post(requestBody)
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
          .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
          .build();

      Response response = client.newCall(httpRequest).execute();

      notifiRepository.save(Notification.builder()
          .memberId(memberId.toString())
          .type(NotificationType.PIN)
          .message(sendPinDto.getBody())
          .createdAt(LocalDateTime.now())
          .build());

      log.info("핀 알림 전송 성공: {}", Objects.requireNonNull(response.body()).string());
    } catch (IOException e) {
      throw new NotiException(ErrorCode.PIN_NOTIFICATION_FAILED);
    }
  }

  @Override
  @Transactional
  public void sendReportNotification(Long memberId, ReportDto.Form reportForm) {

    List<Member> memberList = memberRepository.findAllInActive(reportForm.getLatitude(),
        reportForm.getLongitude(), DISTANCE);
    List<String> tokenList = memberList.stream().map(Member::getFcmToken).collect(Collectors.toList());

    SendReportDto sendReportDto = new SendReportDto(reportForm.getDescriptions());

    MulticastMessage message = makeMultipleMessage(sendReportDto, tokenList);

    try {
      BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
      log.info("게시글 알림 성공 successCount: " + response.getSuccessCount());
    } catch (FirebaseMessagingException e) {
      log.error("알림 전송 실패 : " + e.getMessage());
      throw new NotiException(ErrorCode.REPORT_NOTIFICATION_FAILED);
    }

    List<Notification> notificationList = new ArrayList<>();

    for (Member member : memberList) {
      notificationList.add(
          Notification.builder()
              .memberId(member.getId().toString())
              .type(NotificationType.REPORT)
              .message(sendReportDto.getBody())
              .createdAt(LocalDateTime.now())
              .build()
      );
    }

    notifiRepository.saveAll(notificationList);
  }

  @Override
  @Transactional
  public void sendChatNotification(String chatRoomId, String senderId,
      ChatMessageDto.Request request) {
    try {

      String[] memberIdList = chatRoomId.split("_");
      Long memberId = memberIdList[0].equals(senderId) ? Long.parseLong(memberIdList[0])
          : Long.parseLong(memberIdList[2]);

      Member member = memberRepository.findById(memberId)
          .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
      String token = member.getFcmToken();

      String memberNickname = member.getNickname();
      String body = request.getContent();

      SendChatDto sendChatDto = new SendChatDto(memberNickname, body, token);
      SendSingleDto sendSingleDto = SendSingleDto.builder()
          .title(sendChatDto.getTitle())
          .body(sendChatDto.getBody())
          .token(sendChatDto.getToken())
          .build();

      String message = makeSingleMessage(sendSingleDto);

      OkHttpClient client = new OkHttpClient();

      RequestBody requestBody = RequestBody.create(message,
          MediaType.get("application/json; charset=utf-8"));
      Request httpRequest = new Request.Builder()
          .url(FCM_API_URL)
          .post(requestBody)
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
          .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
          .build();

      Response response = client.newCall(httpRequest).execute();

      notifiRepository.save(Notification.builder()
          .memberId(Long.toString(memberId))
          .type(NotificationType.CHAT)
          .message(sendSingleDto.getBody())
          .createdAt(LocalDateTime.now())
          .build());

      log.info( " 채팅 알림 전송 성공: {}", Objects.requireNonNull(response.body()).string());
    } catch (IOException e) {
      throw new NotiException(ErrorCode.PIN_NOTIFICATION_FAILED);
    }
  }

  @Override
  public List<NotificationDto> getNotificationList(Long memberId) {

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

    return notifiRepository.findAllByMemberId(memberId.toString())
        .stream()
        .map(NotificationDto::fromEntity)
        .collect(Collectors.toList());
  }

  private String getAccessToken() throws IOException {
    try {
      GoogleCredentials googleCredentials = GoogleCredentials
          .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
          .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

      googleCredentials.refreshIfExpired();

      return googleCredentials.refreshAccessToken().getTokenValue();
    } catch (IOException e) {
      throw new NotiException(ErrorCode.ACCESS_TOKEN_ERROR);
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

  private static MulticastMessage makeMultipleMessage(SendReportDto request,
      List<String> tokenList) {
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
