package com.samcomo.dbz.member.jwt.filter;

import static com.samcomo.dbz.global.exception.ErrorCode.INVALID_SESSION;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.constants.MemberRole;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
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

  private final static String REISSUE_URI = "/member/reissue";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = getAccessToken(request);

    if (!isAuthenticationRequired(accessToken) || isReIssueURI(request)) {
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

  private boolean isReIssueURI(HttpServletRequest request) {
    return REISSUE_URI.equals(request.getRequestURI());
  }

  private String getAccessToken(HttpServletRequest request) {
    return request.getHeader(ACCESS_TOKEN.getKey());
  }

  private void validateAccessToken(String accessToken) {
    jwtUtil.validateToken(accessToken, ACCESS_TOKEN);
  }

  private MemberDetails getMemberDetails(String accessToken) {
    Long memberId = getMemberId(accessToken);
    MemberRole role = getMemberRole(accessToken);

    return new MemberDetails(Member.builder()
        .id(memberId)
        .role(role)
        .build());
  }

  private boolean isAuthenticationRequired(String accessToken) {
    return (accessToken != null && accessToken.trim().length() > 0);
  }

  private Long getMemberId(String accessToken) {
    return Long.valueOf(jwtUtil.getId(accessToken));
  }

  private MemberRole getMemberRole(String accessToken) {
    return MemberRole.get(jwtUtil.getRole(accessToken))
        .orElseThrow(() -> new MemberException(INVALID_SESSION));
  }
}
