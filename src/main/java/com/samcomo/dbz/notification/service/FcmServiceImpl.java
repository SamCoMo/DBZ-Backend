package com.samcomo.dbz.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.notification.exception.NotiException;
import com.samcomo.dbz.notification.model.constants.PinMessage;
import com.samcomo.dbz.notification.model.dto.FcmMessageDto;
import com.samcomo.dbz.notification.model.dto.SendPinDto;
import java.io.IOException;
import java.util.List;
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

      SendPinDto sendPinDto = SendPinDto.builder()
          .token("tokentoken")
          .title(PinMessage.PIN_MESSAGE.getTitle())
          .body(PinMessage.PIN_MESSAGE.getBody())
          .build();
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

      log.info("핀 알림 전송 성공: {}", response.body().string());
    }catch(IOException e){
      throw new NotiException(ErrorCode.PIN_NOTIFICATION_FAILED);
    }
  }

  @Override
  public void sendReportNotification(){

  }

  private String getAccessToken() throws IOException {
    try {
      GoogleCredentials googleCredentials = GoogleCredentials
          .fromStream(new ClassPathResource(SERVICE_ACCOUNT_JSON).getInputStream())
          .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

      googleCredentials.refreshIfExpired();

      return googleCredentials.refreshAccessToken().getTokenValue();
    } catch (IOException e) {
      throw new IllegalArgumentException("실패");
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

}
