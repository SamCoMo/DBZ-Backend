package com.samcomo.dbz.pin.service;

import com.samcomo.dbz.pin.dto.RegisterPinDto.Request;
import com.samcomo.dbz.pin.dto.RegisterPinDto.Response;
import com.samcomo.dbz.pin.dto.PinDto;
import com.samcomo.dbz.pin.dto.PinListDto;
import java.util.List;
import com.samcomo.dbz.pin.dto.UpdatePinDto;

public interface PinService {

  // 핀 생성
  Response registerPin(Long memberId, Long reportId, Request request);

  // 핀 업데이트
  PinDto updatePin(Long memberId, Long pinId, UpdatePinDto.Request request);

  // 핀 삭제
  void deletePin(Long memberId, Long pinId);

  // 게시물 - 핀 리스트 가져오기
  List<PinListDto> getPinList(Long memberId, Long reportId);

  // 핀 상세정보 가져오기
  PinDto getPin(Long memberId, Long pinId);
}
