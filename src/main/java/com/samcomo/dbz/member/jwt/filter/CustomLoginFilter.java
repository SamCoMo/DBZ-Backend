package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.AUTHENTICATION_FAILED;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_FCM_TOKEN;
import static org.springframework.http.HttpMethod.POST;

import com.samcomo.dbz.member.exception.MemberException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

@Slf4j
public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String LOGIN_URI = "/member/login";
  private static final String EMAIL_KEY = "email";
  private static final String PASSWORD_KEY = "password";
  private static final String FCM_TOKEN_KEY = "fcmToken";

  private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER =
      new AntPathRequestMatcher(LOGIN_URI, POST.name());

  public CustomLoginFilter(
      AuthenticationManager manager, AuthenticationSuccessHandler successHandler) {
    super(LOGIN_REQUEST_MATCHER, manager);
    setAuthenticationSuccessHandler(successHandler);
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

    String fcmToken = obtainFcmToken(request);
    if (!StringUtils.hasText(fcmToken)) {
      throw new MemberException(INVALID_FCM_TOKEN);
    }

    String email = obtainEmail(request);
    email = (email != null) ? email.trim() : "";
    String password = obtainPassword(request);
    password = (password != null) ? password.trim() : "";
    log.info("[기본로그인] /member/login : {}",
        "email : " + email + " password : " + password + "fcmToken : " + fcmToken);

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

  protected String obtainFcmToken(HttpServletRequest request) {
    return request.getParameter(FCM_TOKEN_KEY);
  }
  // 로그인 성공 시 JWT 발급
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws ServletException, IOException {
    getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    throw new MemberException(AUTHENTICATION_FAILED);
  }
}