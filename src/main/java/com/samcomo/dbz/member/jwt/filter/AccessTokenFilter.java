package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.UriKey.REISSUE;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (!isAuthenticationRequired(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String accessToken = getAccessToken(request);
    MemberDetails memberDetails = jwtUtil.extractMemberDetailsFrom(accessToken);

    Authentication authToken = new UsernamePasswordAuthenticationToken(
        memberDetails, null, memberDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }

  private boolean isReIssueURI(HttpServletRequest request) {
    return REISSUE.getUri().equals(request.getRequestURI());
  }

  private String getAccessToken(HttpServletRequest request) {
//    if(request.getHeader(ACCESS_TOKEN.getKey()).startsWith("Bearer")){
//      return request.getHeader(ACCESS_TOKEN.getKey()).substring(7);
//    }
    return request.getHeader(ACCESS_TOKEN.getKey());
  }

  private boolean isAuthenticationRequired(HttpServletRequest request) {
    if (isReIssueURI(request)) {
      return false;
    }
    return isAccessTokenPresent(getAccessToken(request));
  }

  private boolean isAccessTokenPresent(String accessToken) {
    return accessToken != null && accessToken.trim().length() > 0;
  }
}
