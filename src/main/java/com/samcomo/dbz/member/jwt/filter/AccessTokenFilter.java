package com.samcomo.dbz.member.jwt.filter;

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

    String accessToken = request.getHeader(ACCESS_TOKEN.getKey());

    if (!validateAccessToken(request, response, filterChain, accessToken)) {
      return;
    }

    Long memberId = Long.valueOf(jwtUtil.getId(accessToken));
    MemberRole role = MemberRole.get(jwtUtil.getRole(accessToken))
        .orElseThrow(() -> new MemberException(INVALID_SESSION));

    Member member = Member.builder()
        .id(memberId)
        .role(role)
        .build();

    MemberDetails memberDetails = new MemberDetails(member);

    Authentication authToken = new UsernamePasswordAuthenticationToken(
        memberDetails, null, memberDetails.getAuthorities());

    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }

  private boolean validateAccessToken(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain, String accessToken) throws IOException, ServletException {

    if (accessToken == null) {
      filterChain.doFilter(request, response);
      return false;
    }

    // 유효기간 검증
    try {
      jwtUtil.isExpired(accessToken);

    } catch (ExpiredJwtException e) {
      throw new MemberException(ErrorCode.ACCESS_TOKEN_EXPIRED);
    }

    // refresh 토큰 접근 방지
    if (!jwtUtil.getTokenType(accessToken).equals(ACCESS_TOKEN.getKey())) {
      throw new MemberException(ErrorCode.INVALID_ACCESS_TOKEN);
    }

    return true;
  }
}
