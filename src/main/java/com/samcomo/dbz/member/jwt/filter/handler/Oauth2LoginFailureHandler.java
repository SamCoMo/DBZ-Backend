package com.samcomo.dbz.member.jwt.filter.handler;

import static com.samcomo.dbz.global.exception.ErrorCode.SOCIAL_AUTHENTICATION_FAILED;

import com.samcomo.dbz.member.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class Oauth2LoginFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)  {
    throw new MemberException(SOCIAL_AUTHENTICATION_FAILED);
  }
}
