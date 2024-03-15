package com.samcomo.dbz.pin.controller;

import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.pin.dto.CreatePinDto;
import com.samcomo.dbz.pin.dto.UpdatePinAddressDto;
import com.samcomo.dbz.pin.dto.UpdatePinDataDto;
import com.samcomo.dbz.pin.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity<CreatePinDto.Response> registerPin(
      @AuthenticationPrincipal Member member,
      @ModelAttribute CreatePinDto.Request request,
      @RequestParam Long reportId
  ){
    // Authentication 검증
    String memberEmail = member.getEmail();

    CreatePinDto.Response createPinResponse = pinService.createPin(memberEmail,reportId,request);
    return ResponseEntity.ok(createPinResponse);
  }

  // Pin 주소 업데이트 (프론트엔드 - 카카오 API 사용) : 보류
  @PutMapping("/{pinId}/address")
  public ResponseEntity<UpdatePinAddressDto.Response> updatePinAddress(
      @AuthenticationPrincipal Member member,
      @ModelAttribute UpdatePinAddressDto.Request request,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = member.getEmail();

    UpdatePinAddressDto.Response updatePinAddressDto = pinService.updatePinAddress(memberEmail,pinId,request);
    return ResponseEntity.ok(updatePinAddressDto);
  }

  // Pin 수정
  @PutMapping("/{pinId}")
  public ResponseEntity<UpdatePinDataDto.Response> updatePinData(
      @AuthenticationPrincipal Member member,
      @ModelAttribute UpdatePinDataDto.Response updatePinResponseDto,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = member.getEmail();

    UpdatePinDataDto.Response updatePinDto = pinService.updatePinData(memberEmail,pinId,updatePinResponseDto);
    return ResponseEntity.ok(updatePinDto);
  }


  // Pin 삭제
  @DeleteMapping("/{pinId}")
  public ResponseEntity<Void> deletePin(
      @AuthenticationPrincipal Member member,
      @PathVariable Long pinId
  ){
    // Authentication 검증
    String memberEmail = member.getEmail();

    pinService.deletePin(memberEmail,pinId);
    return ResponseEntity.noContent().build();
  }
}
