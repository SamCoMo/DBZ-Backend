package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.RefreshToken;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER =
      new AntPathRequestMatcher("/member/login", "POST");

  private static final String EMAIL_KEY = "email";
  private static final String PASSWORD_KEY = "password";

  public CustomLoginFilter(
      AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
    super(LOGIN_REQUEST_MATCHER, authenticationManager);
    this.jwtUtil = jwtUtil;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    String email = obtainEmail(request);
    email = (email != null) ? email.trim() : "";
    String password = obtainPassword(request);
    password = (password != null) ? password.trim() : "";

    UsernamePasswordAuthenticationToken authToken =
        UsernamePasswordAuthenticationToken.unauthenticated(email, password);

    // 검증을 위해 AuthenticationManager 로 email, password 를 담은 토큰을 전달.
    return this.getAuthenticationManager().authenticate(authToken);
  }

  protected String obtainEmail(HttpServletRequest request) {
    return request.getParameter(EMAIL_KEY);
  }

  protected String obtainPassword(HttpServletRequest request) {
    return request.getParameter(PASSWORD_KEY);
  }

  // 로그인 성공 시 JWT 발급
  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain, Authentication authResult) {

    String memberId = getMemberId(authResult);
    String role = getMemberRole(authResult);

    String accessToken = jwtUtil.createToken(ACCESS_TOKEN, memberId, role);
    String refreshToken = jwtUtil.createToken(REFRESH_TOKEN, memberId, role);

    saveRefreshTokenToDataBase(memberId, refreshToken);

    response.setHeader(ACCESS_TOKEN.getKey(), accessToken);
    response.addCookie(createCookie(refreshToken));
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private String getMemberId(Authentication authResult) {
    MemberDetails memberDetails = (MemberDetails) authResult.getPrincipal();
    return String.valueOf(memberDetails.getId());
  }

  private String getMemberRole(Authentication authResult) {
    Iterator<? extends GrantedAuthority> iterator = authResult.getAuthorities().iterator();
    GrantedAuthority auth = iterator.next();
    return auth.getAuthority();
  }

  // 로그인 실패 응답 간이 구현
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    throw new MemberException(ErrorCode.AUTHENTICATION_FAILED);
  }

  private Cookie createCookie(String refreshToken) {
    Cookie cookie = new Cookie(REFRESH_TOKEN.getKey(), refreshToken);
    cookie.setMaxAge(24 * 60 * 60);
    // cookie.setSecure(true); csrf 공격 방지
    cookie.setHttpOnly(true); // xss 공격 방지 (js 접근 불가)
    return cookie;
  }

  private void saveRefreshTokenToDataBase(String memberId, String refreshToken) {
    Date expiration = new Date(System.currentTimeMillis() + REFRESH_TOKEN.getExpiredMs());

    refreshTokenRepository.save(RefreshToken.builder()
        .memberId(Long.valueOf(memberId))
        .refreshToken(refreshToken)
        .expiration(String.valueOf(expiration))
        .build());
  }
}