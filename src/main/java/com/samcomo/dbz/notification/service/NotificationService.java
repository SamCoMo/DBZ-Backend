package com.samcomo.dbz.notification.service;

import com.samcomo.dbz.chat.dto.ChatMessageDto;
import com.samcomo.dbz.notification.model.dto.NotificationDto;
import com.samcomo.dbz.report.model.dto.ReportDto;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
  void sendPinNotification(Long memberId);

  void sendReportNotification(Long memberId, ReportDto.Form reportForm);

  List<NotificationDto> getNotificationList(Long memberId);

  void sendChatNotification(String chatRoomId, String senderId, ChatMessageDto.Request request);
}
