package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

  private final JwtUtil jwtUtil;
  private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER =
      new AntPathRequestMatcher("/member/login", "POST");

  private static final String EMAIL_KEY = "email";
  private static final String PASSWORD_KEY = "password";

  private static final Long EXPIRATION_ACCESS_TOKEN = 6000L * 10 * 1000; // 10분
  private static final Long EXPIRATION_REFRESH_TOKEN = 6000L * 10 * 60 * 24; // 24시간

  public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {

    super(LOGIN_REQUEST_MATCHER, authenticationManager);
    this.jwtUtil = jwtUtil;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

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
      HttpServletResponse response, FilterChain chain, Authentication authResult)
      throws IOException, ServletException {

    MemberDetails memberDetails = (MemberDetails) authResult.getPrincipal();
    String id = String.valueOf(memberDetails.getId());
    String email = memberDetails.getEmail();

    Iterator<? extends GrantedAuthority> iterator = authResult.getAuthorities().iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    String accessToken = jwtUtil.createToken(
        ACCESS_TOKEN, id, role, email, EXPIRATION_ACCESS_TOKEN);
    String refreshToken = jwtUtil.createToken(
        REFRESH_TOKEN, id, role, email, EXPIRATION_REFRESH_TOKEN);

    response.setHeader(ACCESS_TOKEN.getKey(), accessToken);
    response.addCookie(createCookie(REFRESH_TOKEN.getKey(), refreshToken));
    response.setStatus(HttpStatus.OK.value());
  }

  // 로그인 실패 응답 간이 구현
  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed)
      throws IOException, ServletException {

    response.setStatus(401);
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60);
    // cookie.setSecure(true); csrf 공격 방지 (https 가 적용되지 않은 이미지 경로 접근 방지)
    cookie.setHttpOnly(true); // xss 공격 방지 (js 접근 불가)

    return cookie;
  }
}