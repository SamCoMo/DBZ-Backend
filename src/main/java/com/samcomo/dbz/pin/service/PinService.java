package com.samcomo.dbz.pin.service;

import com.samcomo.dbz.pin.dto.CreatePinDto.Request;
import com.samcomo.dbz.pin.dto.CreatePinDto.Response;
import com.samcomo.dbz.pin.dto.PinListDto;
import com.samcomo.dbz.pin.dto.UpdatePinAddressDto;
import com.samcomo.dbz.pin.dto.UpdatePinDataDto;
import java.util.List;

public interface PinService {

  // 핀 생성
  Response createPin(String memberEmail,Long reportId, Request request);

  // 핀 주소 업데이트
  UpdatePinAddressDto.Response updatePinAddress(String memberEmail, Long pinId, UpdatePinAddressDto.Request request);

  // 핀 업데이트 (주소는 바꿀수 없습니다.)
  UpdatePinDataDto.Response updatePinData(String memberEmail, Long pinId, UpdatePinDataDto.Response updatePinResponseDto);

  // 핀 삭제
  void deletePin(String memberEmail, Long pinId);

  //TODO 게시물 - 핀 리스트 가져오기
  List<PinListDto> getPinList(String memberEmail, Long reportId);

  //TODO 핀 상세정보 가져오기
}
