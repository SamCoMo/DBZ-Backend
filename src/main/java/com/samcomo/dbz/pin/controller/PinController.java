package com.samcomo.dbz.pin.controller;

import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.pin.dto.PinDto;
import com.samcomo.dbz.pin.dto.PinListDto;
import com.samcomo.dbz.pin.dto.RegisterPinDto;
import com.samcomo.dbz.pin.dto.UpdatePinDto;
import com.samcomo.dbz.pin.service.PinService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PinController {

  private final PinService pinService;

  // Pin 생성
  @PostMapping()
  public ResponseEntity<RegisterPinDto.Response> registerPin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @ModelAttribute RegisterPinDto.Request request,
      @RequestParam Long reportId
  ) {
    log.info("<<< ReportController - Register Report >>>");

    log.info("multipartFileList : {}", request.getMultipartFileList());
    log.info("latitude : {}", request.getLatitude());
    log.info("longitude : {}", request.getLongitude());
    log.info("address : {}", request.getAddress());
    log.info("description : {}", request.getDescription());

    RegisterPinDto.Response createPinResponse =
        pinService.registerPin(memberDetails.getId(), reportId, request);
    return ResponseEntity.ok(createPinResponse);
  }

  // Pin 수정
  @PutMapping("/{pinId}")
  public ResponseEntity<PinDto> updatePin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @ModelAttribute UpdatePinDto.Request request,
      @PathVariable Long pinId
  ) {
    PinDto updatedPinDto =
        pinService.updatePin(memberDetails.getId(), pinId, request);
    return ResponseEntity.ok(updatedPinDto);
  }

  // Pin 삭제
  @DeleteMapping("/{pinId}")
  public ResponseEntity<Void> deletePin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long pinId
  ) {
    pinService.deletePin(memberDetails.getId(), pinId);
    return ResponseEntity.ok().build();
  }

  // Report 의 Pin List 가져오기
  @GetMapping("/report/{reportId}/pin-list")
  public ResponseEntity<List<PinListDto>> getPinList(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long reportId
  ) {
    List<PinListDto> pinListDtoList = pinService.getPinList(memberDetails.getId(), reportId);
    return ResponseEntity.ok(pinListDtoList);
  }

  // Pin 상세정보 가져오기
  @GetMapping("/{pinId}")
  public ResponseEntity<PinDto> getPin(
      @AuthenticationPrincipal MemberDetails memberDetails,
      @PathVariable Long pinId
  ) {
    PinDto pinDto = pinService.getPin(memberDetails.getId(), pinId);
    return ResponseEntity.ok(pinDto);
  }
}
