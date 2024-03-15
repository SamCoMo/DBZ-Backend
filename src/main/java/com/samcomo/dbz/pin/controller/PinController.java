package com.samcomo.dbz.pin.controller;

import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.pin.dto.RegisterPinDto;
import com.samcomo.dbz.pin.dto.UpdatePinDto;
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
  public ResponseEntity<RegisterPinDto.Response> registerPin(
      @AuthenticationPrincipal MemberDetails details,
      @ModelAttribute RegisterPinDto.Request request,
      @RequestParam Long reportId
  ){

    RegisterPinDto.Response createPinResponse =
        pinService.registerPin(details.getId(), reportId, request);

    return ResponseEntity.ok(createPinResponse);
  }

  // Pin 수정
  @PutMapping("/{pinId}")
  public ResponseEntity<UpdatePinDto.Response> updatePinData(
      @AuthenticationPrincipal MemberDetails details,
      @ModelAttribute UpdatePinDto.Response updatePinResponseDto,
      @PathVariable Long pinId
  ){

    UpdatePinDto.Response updatePinDto =
        pinService.updatePin(details.getId(), pinId, updatePinResponseDto);

    return ResponseEntity.ok(updatePinDto);
  }


  // Pin 삭제
  @DeleteMapping("/{pinId}")
  public ResponseEntity<Void> deletePin(
      @AuthenticationPrincipal MemberDetails details,
      @PathVariable Long pinId
  ){

    pinService.deletePin(details.getId(), pinId);

    return ResponseEntity.ok().build();
  }
}
