package com.samcomo.dbz.member.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.LocationRequest;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
@Tag(name = "회원 관리 컨트롤러", description = "회원 관련 API")
public class MemberController {

  private final MemberService memberService;
  private final RefreshTokenFilter refreshTokenFilter;

  @PostMapping("/register")
  @Operation(summary = "신규 회원 가입")
  public ResponseEntity<Void> register(
      @Valid @RequestBody RegisterRequest request) {

    log.info("[회원가입] /member/register {}", request.getEmail());
    memberService.register(request);

    return ResponseEntity.status(CREATED).build();
  }

  @GetMapping("/my")
  @Operation(summary = "회원 마이페이지")
  public ResponseEntity<MyPageResponse> getMyInfo(
      @AuthenticationPrincipal MemberDetails details) {

    log.info("[마이페이지] /member/my : {}", details.getId());
    MyPageResponse myPageResponse = memberService.getMyInfo(details.getId());

    return ResponseEntity.ok(myPageResponse);
  }

  @PostMapping("/reissue")
  @Operation(summary = "access 토큰 재발급")
  public ResponseEntity<Void> reissue(
      @CookieValue(value = "Refresh-Token", required = false) String refreshToken,
      HttpServletResponse response) {

    log.info("[토큰재발급] /member/reissue : {}", refreshToken);
    refreshTokenFilter.reissue(refreshToken, response);

    return ResponseEntity.status(CREATED).build();
  }

  @PatchMapping("/location")
  @Operation(summary = "회원 위치 업데이트")
  public ResponseEntity<Void> updateLocation(
      @AuthenticationPrincipal MemberDetails details,
      @Valid @RequestBody LocationRequest request) {

    log.info("[회원위치 업데이트] /member/location : {}", request.getAddress());
    memberService.updateLocation(details.getId(), request);

    return ResponseEntity.status(OK).build();
  }

  @PatchMapping("/profile-image")
  @Operation(summary = "프로필 이미지 업데이트")
  public ResponseEntity<Void> updateProfileImage(
      @AuthenticationPrincipal MemberDetails details,
      @RequestPart MultipartFile profileImage) {

    log.info("[프로필 이미지 업데이트] /member/profile-image: {}", profileImage.getName());
    memberService.updateProfileImage(details.getId(), profileImage);

    return ResponseEntity.status(OK).build();
  }
}
