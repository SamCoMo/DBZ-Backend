package com.samcomo.dbz.member.controller;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN_KEY;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.LocationUpdateRequest;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.dto.MyPageResponse;
import com.samcomo.dbz.member.model.dto.RegisterRequest;
import com.samcomo.dbz.member.model.dto.TokenValidationResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Tag(name = "회원 관리 컨트롤러", description = "회원 관련 API")
public class MemberController {

  private final MemberService memberService;
  private final RefreshTokenFilter refreshTokenFilter;
  private final JwtUtil jwtUtil;

  @PostMapping("/register")
  @Operation(summary = "신규 회원 가입")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {

    memberService.register(request);

    return ResponseEntity.status(CREATED).build();
  }

  @GetMapping("/my")
  @Operation(summary = "회원 마이페이지")
  public ResponseEntity<MyPageResponse> getMyInfo(
      @AuthenticationPrincipal MemberDetails details) {

    MyPageResponse myPageResponse = memberService.getMyInfo(details.getId());

    return ResponseEntity.ok(myPageResponse);
  }

  @PostMapping("/reissue")
  @Operation(summary = "access 토큰 재발급")
  public ResponseEntity<Void> reissue(
      @CookieValue(value = "Refresh-Token", required = false) String refreshToken,
      HttpServletResponse response) {

    refreshTokenFilter.reissue(refreshToken, response);

    return ResponseEntity.status(CREATED).build();
  }

  @GetMapping("/validate-token")
  @Operation(summary = " access 토큰 유효성 검사")
  public ResponseEntity<TokenValidationResponse> validateAccessToken(
      @RequestHeader(ACCESS_TOKEN_KEY) String accessToken
  ){
    TokenValidationResponse tokenValidationResponse
        = jwtUtil.getTokenValidationResponse(accessToken, ACCESS_TOKEN);
    return ResponseEntity.ok(tokenValidationResponse);
  }

  @PatchMapping("/location")
  @Operation(summary = "회원 위치 업데이트")
  public ResponseEntity<Void> updateLocation(
      @AuthenticationPrincipal MemberDetails details,
      @Valid @RequestBody LocationUpdateRequest request) {

    memberService.updateLocation(details.getId(), request);

    return ResponseEntity.status(OK).build();
  }
}
