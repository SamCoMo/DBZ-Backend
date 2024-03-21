package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.ACCESS_TOKEN_EXPIRED;
import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_SESSION;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import io.jsonwebtoken.ExpiredJwtException;
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

    String accessToken = getAccessToken(request);
    // 아직 로그인하지 않은 경우
    if (isNull(accessToken)) {
      filterChain.doFilter(request, response);
      return;
    }

    validateAccessToken(accessToken);

    MemberDetails memberDetails = getMemberDetails(accessToken);
    Authentication authToken = new UsernamePasswordAuthenticationToken(
        memberDetails, null, memberDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }

  private String getAccessToken(HttpServletRequest request) {
    return request.getHeader(ACCESS_TOKEN.getKey());
  }

  private void validateAccessToken(String accessToken) {
    try {
      checkExpiration(accessToken);
    } catch (ExpiredJwtException e) {
      throw new MemberException(ACCESS_TOKEN_EXPIRED);
    }

    if (!isTokenTypeCorrect(accessToken)) {
      throw new MemberException(ErrorCode.INVALID_ACCESS_TOKEN);
    }
  }

  private MemberDetails getMemberDetails(String accessToken) {
    Long memberId = getMemberId(accessToken);
    MemberRole role = getMemberRole(accessToken);

    return new MemberDetails(Member.builder()
        .id(memberId)
        .role(role)
        .build());
  }

  public boolean isNull(String accessToken) {
    return accessToken == null;
  }

  public void checkExpiration(String accessToken) throws ExpiredJwtException {
    jwtUtil.isExpired(accessToken);
  }


  public boolean isTokenTypeCorrect(String accessToken) {
    return jwtUtil.getTokenType(accessToken).equals(ACCESS_TOKEN.getKey());
  }

  private Long getMemberId(String accessToken) {
    return Long.valueOf(jwtUtil.getId(accessToken));
  }

  private MemberRole getMemberRole(String accessToken) {
    return MemberRole.get(jwtUtil.getRole(accessToken))
        .orElseThrow(() -> new MemberException(INVALID_SESSION));
  }
}
