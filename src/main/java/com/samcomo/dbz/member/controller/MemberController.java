package com.samcomo.dbz.member.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.samcomo.dbz.member.jwt.filter.RefreshTokenFilter;
import com.samcomo.dbz.member.model.dto.RegisterRequestDto;
import com.samcomo.dbz.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
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

  @PostMapping("/reissue")
  @Operation(summary = "access 토큰 재발급")
  public ResponseEntity<Void> reissue(
      @CookieValue("Refresh-Token") String refreshToken, HttpServletResponse response) {

    refreshTokenFilter.reissue(refreshToken, response);

    return ResponseEntity.status(CREATED).build();
  }
}
