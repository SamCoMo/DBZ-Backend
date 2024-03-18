package com.samcomo.dbz.notification.service;

import com.samcomo.dbz.report.model.dto.ReportDto;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public interface FcmService {
  void sendPinNotification() throws IOException;

  void sendReportNotification(ReportDto.Form reportForm);

//  void sendChatNotification();
}
