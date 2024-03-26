package com.samcomo.dbz.notification.service;

import com.samcomo.dbz.chat.model.dto.ChatMessageDto;
import com.samcomo.dbz.notification.model.dto.NotificationDto;
import com.samcomo.dbz.report.model.dto.ReportDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

  void sendPinNotification(Long memberId);

  void sendReportNotification(ReportDto.Form reportForm);

  List<NotificationDto> getNotificationList(String memberId);

  void sendChatNotification(String chatRoomId, String senderId, ChatMessageDto.Request request);
}
