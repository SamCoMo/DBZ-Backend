package com.samcomo.dbz.pin.controller;

import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.pin.dto.RegisterPinDto;
import com.samcomo.dbz.pin.dto.PinDto;
import com.samcomo.dbz.pin.dto.PinListDto;
import com.samcomo.dbz.pin.dto.UpdatePinAddressDto;
import com.samcomo.dbz.pin.dto.UpdatePinDataDto;
import com.samcomo.dbz.pin.service.PinService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pin")
public class PinController {

  private final PinService pinService;

  // Pin 생성
  @PostMapping()
  public ResponseEntity<RegisterPinDto.Response> registerPin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @ModelAttribute RegisterPinDto.Request request,
      @RequestParam Long reportId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    RegisterPinDto.Response createPinResponse = pinService.createPin(memberEmail,reportId,request);
    return ResponseEntity.ok(createPinResponse);
  }

  // Pin 주소 업데이트 (프론트엔드 - 카카오 API 사용) : 보류
  @PutMapping("/{pinId}/address")
  public ResponseEntity<UpdatePinAddressDto.Response> updatePinAddress(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @ModelAttribute UpdatePinAddressDto.Request request,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    UpdatePinAddressDto.Response updatePinAddressDto = pinService.updatePinAddress(memberEmail,pinId,request);
    return ResponseEntity.ok(updatePinAddressDto);
  }

  // Pin 수정
  @PutMapping("/{pinId}")
  public ResponseEntity<UpdatePinDataDto.Response> updatePinData(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @ModelAttribute UpdatePinDataDto.Response updatePinResponseDto,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    UpdatePinDataDto.Response updatePinDto = pinService.updatePinData(memberEmail,pinId,updatePinResponseDto);
    return ResponseEntity.ok(updatePinDto);
  }


  // Pin 삭제
  @DeleteMapping("/{pinId}")
  public ResponseEntity<Void> deletePin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    pinService.deletePin(memberEmail,pinId);
    return ResponseEntity.noContent().build();
  }

  // Report 의 Pin List 가져오기
  @GetMapping("/list/report/{reportId}/")
  public ResponseEntity<List<PinListDto>> getPinList(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long reportId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    List<PinListDto> pinListDtoList = pinService.getPinList(memberEmail,reportId);
    return ResponseEntity.ok(pinListDtoList);
  }

  // Pin 상세정보 가져오기
  @GetMapping("/{pinId}")
  public ResponseEntity<PinDto> getPin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = memberDetails.getEmail();

    PinDto pinDto = pinService.getPin(memberEmail,pinId);
    return ResponseEntity.ok(pinDto);
  }
}
