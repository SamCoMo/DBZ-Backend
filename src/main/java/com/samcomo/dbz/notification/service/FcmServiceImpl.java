package com.samcomo.dbz.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.notification.exception.NotiException;
import com.samcomo.dbz.notification.model.dto.FcmMessageDto;
import com.samcomo.dbz.notification.model.dto.SendPinDto;
import com.samcomo.dbz.notification.model.dto.SendReportDto;
import com.samcomo.dbz.report.model.dto.ReportDto;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

  @Value("${fcm.key.path}")
  private String SERVICE_ACCOUNT_JSON;
  @Value("${fcm.api.url}")
  private String FCM_API_URL;

  @Override
  public void sendPinNotification() {

    try{

      //TODO: 유저 토큰값 서치
      String token = "토큰";

      SendPinDto sendPinDto = new SendPinDto(token);
      String message = makeSingleMessage(sendPinDto);

      OkHttpClient client = new OkHttpClient();

      RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
      Request httpRequest = new Request.Builder()
          .url(FCM_API_URL)
          .post(requestBody)
          .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
          .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
          .build();

      Response response = client.newCall(httpRequest).execute();

      log.info("핀 알림 전송 성공: {}", Objects.requireNonNull(response.body()).string());
    }catch(IOException e){
      throw new NotiException(ErrorCode.PIN_NOTIFICATION_FAILED);
    }
  }

  @Override
  public void sendReportNotification(ReportDto.Form reportForm){

    String token = "토큰";

    SendReportDto sendReportDto = new SendReportDto(token, reportForm.getDescriptions().substring(0,20));

    //게시글 주변 회원 검색 & token get
//    List<String> tokenList = memberRepository.findAllInActive(reportForm.getLatitude(), reportForm.getLongitude(), DISTANCE);

    List<String> tokenList = List.of("e-HB1jbPIPQvJ0-taOAJB7:APA91bGoJAUyLMgEEHlieBOArqLSp-6RNySt5JSWdvMHoKq3xAu1TsE2FZeVJl1X6P3ElT11D9w8Ar36TO69jvOrV6fgi0FSGfqvdDBNBkJ9PwkGZbCWSdyZ8zd7W76ybkVyuEvtoVgP",
        "e-HB1jbPIPQvJ0-taOAJB7:APA91bGoJAUyLMgEEHlieBOArqLSp-6RNySt5JSWdvMHoKq3xAu1TsE2FZeVJl1X6P3ElT11D9w8Ar36TO69jvOrV6fgi0FSGfqvdDBNBkJ9PwkGZbCWSdyZ8zd7W76ybkVyuEvtoVgP",
        "e-HB1jbPIPQvJ0-taOAJB7:APA91bGoJAUyLMgEEHlieBOArqLSp-6RNySt5JSWdvMHoKq3xAu1TsE2FZeVJl1X6P3ElT11D9w8Ar36TO69jvOrV6fgi0FSGfqvdDBNBkJ9PwkGZbCWSdyZ8zd7W76ybkVyuEvtoVgP");
    MulticastMessage message = makeMultipleMessage(sendReportDto, tokenList);

    try{
      BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
      log.info("게시글 알림 성공 successCount: " + response.getSuccessCount());
    } catch (FirebaseMessagingException e) {
      log.error("알림 전송 실패 : " + e.getMessage());
      throw new NotiException(ErrorCode.REPORT_NOTIFICATION_FAILED);
    }
    //TODO: notiDB 저장


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

  private String makeSingleMessage(SendPinDto sendPinDto) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
        .message(FcmMessageDto.Message.builder()
            .token(sendPinDto.getToken())
            .notification(FcmMessageDto.Notification.builder()
                .title(sendPinDto.getTitle())
                .body(sendPinDto.getBody())
                .build()
            ).build()).build();

    return objectMapper.writeValueAsString(fcmMessageDto);
  }

  private static MulticastMessage makeMultipleMessage(SendReportDto request,  List<String> tokenList) {
    MulticastMessage message = MulticastMessage.builder()
        .setNotification(Notification.builder()
            .setTitle(request.getTitle())
            .setBody(request.getBody())
            .build())
        .addAllTokens(tokenList)
        .build();

    log.info("message: {}", request.getBody());
    return message;
  }

}
