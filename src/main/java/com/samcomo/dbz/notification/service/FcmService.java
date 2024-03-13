package com.samcomo.dbz.notification.service;

import com.samcomo.dbz.notification.model.dto.SendPinDto;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public interface FcmService {
  void sendPinMessage() throws IOException;
}
