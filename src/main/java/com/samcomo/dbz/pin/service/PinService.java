package com.samcomo.dbz.pin.service;

import com.samcomo.dbz.pin.dto.RegisterPinDto.Request;
import com.samcomo.dbz.pin.dto.RegisterPinDto.Response;
import com.samcomo.dbz.pin.dto.UpdatePinDto;

public interface PinService {

  // 핀 생성
  Response registerPin(Long memberId, Long reportId, Request request);

  // 핀 업데이트 (주소는 바꿀수 없습니다.)
  UpdatePinDto.Response updatePin(Long memberId, Long pinId, UpdatePinDto.Response updatePinResponseDto);

  // 핀 삭제
  void deletePin(Long memberId, Long pinId);
}
