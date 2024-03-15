package com.samcomo.dbz.notification.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.samcomo.dbz.notification.service.FcmService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
  private final FcmService fcmService;


  @PostMapping("/send")
  public ResponseEntity<Void> pushMessage() throws IOException {
    fcmService.sendPinNotification();

    return ResponseEntity.status(CREATED).build();

  }
}
