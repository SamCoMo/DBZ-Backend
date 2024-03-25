package com.samcomo.dbz.member.jwt.filter;

import static com.google.api.client.http.HttpMethods.POST;
import static com.samcomo.dbz.global.exception.ErrorCode.ALREADY_LOGGED_OUT;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.AuthUtils;
import com.samcomo.dbz.member.jwt.CookieUtil;
import com.samcomo.dbz.member.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;

  private static final String LOGOUT_URI = "\\/member\\/logout";

  public CustomLogoutFilter(AuthUtils authUtils) {
    this.jwtUtil = authUtils.getJwtUtil();
    this.cookieUtil = authUtils.getCookieUtil();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    if (!isLogoutRequest(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String refreshToken = cookieUtil.getRefreshToken(request);
    validateRefreshToken(refreshToken);

    jwtUtil.deleteRefreshTokenFromDB(refreshToken);
    response.setHeader(CookieUtil.COOKIE_KEY, cookieUtil.getNullCookie());
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private void validateRefreshToken(String refreshToken) {
    jwtUtil.validateTokenAndTokenType(refreshToken, REFRESH_TOKEN);
    if (!jwtUtil.isRefreshTokenInDB(refreshToken)) {
      throw new MemberException(ALREADY_LOGGED_OUT);
    }
  }

  private boolean isLogoutRequest(HttpServletRequest request) {
    if (!request.getMethod().equals(POST)) {
      return false;
    }
    return request.getRequestURI().matches(LOGOUT_URI);
  }
}
