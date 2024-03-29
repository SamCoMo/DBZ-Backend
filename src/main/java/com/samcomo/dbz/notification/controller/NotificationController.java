package com.samcomo.dbz.notification.controller;

import com.samcomo.dbz.global.log.LogMethodInvocation;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.notification.model.dto.NotificationDto;
import com.samcomo.dbz.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "알림", description = "알림 API")
@RequestMapping("/notification")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/list")
  @LogMethodInvocation
  @Operation(summary = "알림 목록 조회")
  public ResponseEntity<List<NotificationDto>> getNotificationList(
      @AuthenticationPrincipal MemberDetails details) {

    List<NotificationDto> result =
        notificationService.getNotificationList(details.getIdAsString());

    return ResponseEntity.ok(result);
  }
}
