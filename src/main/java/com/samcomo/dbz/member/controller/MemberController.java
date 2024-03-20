package com.samcomo.dbz.member.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.LocationInfo;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.dto.MyInfo;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원 관리 컨트롤러", description = "회원 관련 API")
public class MemberController {

  private final MemberService memberService;
  private final RefreshTokenFilter refreshTokenFilter;

  @PostMapping("/register")
  @Operation(summary = "신규 회원 가입")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDto request) {

    memberService.register(request);

    return ResponseEntity.status(CREATED).build();
  }

  @GetMapping("/my")
  @Operation(summary = "회원 마이페이지")
  public ResponseEntity<MyInfo> getMyInfo(
      @AuthenticationPrincipal MemberDetails details) {

    MyInfo myInfo = memberService.getMyInfo(details.getId());

    return ResponseEntity.ok(myInfo);
  }

  @PostMapping("/reissue")
  @Operation(summary = "access 토큰 재발급")
  public ResponseEntity<Void> reissue(
      @CookieValue("Refresh-Token") String refreshToken, HttpServletResponse response) {

    refreshTokenFilter.reissue(refreshToken, response);

    return ResponseEntity.status(CREATED).build();
  }

  @PatchMapping("/location")
  @Operation(summary = "회원 위치 업데이트")
  public ResponseEntity<LocationInfo.Response> updateLocation(
      @AuthenticationPrincipal MemberDetails details,
      @Valid @RequestBody LocationInfo.Request request) {

    LocationInfo.Response location = memberService.updateLocation(details.getId(), request);

    return ResponseEntity.ok(location);
  }
}
