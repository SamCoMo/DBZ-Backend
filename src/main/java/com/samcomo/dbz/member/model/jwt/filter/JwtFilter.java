package com.samcomo.dbz.member.model.jwt.filter;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;

import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = request.getHeader(ACCESS_TOKEN.getKey());

    if (!validateAccessToken(request, response, filterChain, accessToken)) {
      return;
    }

    String email = jwtUtil.getEmail(accessToken);
    MemberRole role = MemberRole.get(jwtUtil.getRole(accessToken));

    Member member = Member.builder()
        .email(email)
        .role(role)
        .build();

    MemberDetails memberDetails = new MemberDetails(member);

    Authentication authToken = new UsernamePasswordAuthenticationToken(memberDetails, null,
        memberDetails.getAuthorities());

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
      // TODO refresh 토큰이 유효할 시 access 토큰 재발급 구현 (+ RTR 구현)

      // response body
      PrintWriter writer = response.getWriter();
      writer.print("access token expired");

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    // refresh 토큰 접근 방지
    if (!jwtUtil.getTokenType(accessToken).equals(ACCESS_TOKEN.getKey())) {

      PrintWriter writer = response.getWriter();
      writer.print("invalid access token");

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    return true;
  }
}
