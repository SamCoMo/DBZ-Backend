package com.samcomo.dbz.member.jwt.filter.handler;

import com.samcomo.dbz.member.exception.MemberException;
import com.samcomo.dbz.member.jwt.CookieUtil;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import com.samcomo.dbz.member.model.dto.oauth2.Oauth2MemberDetails;
import com.samcomo.dbz.member.model.entity.Member;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Iterator;

import static com.samcomo.dbz.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.samcomo.dbz.member.model.constants.LoginType.DEFAULT;
import static com.samcomo.dbz.member.model.constants.LoginType.GOOGLE;
import static com.samcomo.dbz.member.model.constants.ParameterKey.COOKIE;
import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static com.samcomo.dbz.member.model.constants.TokenType.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final MemberRepository memberRepository;

  private static final String FCM_TOKEN_KEY = "fcmToken";

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authResult) {

    MemberDetails details = getMemberDetails(authResult);

    Long memberId = details.getId();
    String role = getMemberRole(details);

    jwtUtil.deleteOldRefreshTokenFromDB(memberId);

    String accessToken = jwtUtil.createToken(ACCESS_TOKEN, String.valueOf(memberId), role);
    String refreshToken = jwtUtil.createToken(REFRESH_TOKEN, String.valueOf(memberId), role);

    jwtUtil.saveRefreshTokenToDB(memberId, refreshToken);

    if (details.getLoginType() == DEFAULT) saveFcmToken(memberId, request);

    response.setHeader(ACCESS_TOKEN.getKey(), accessToken);
    response.addHeader(COOKIE.getKey(), cookieUtil.createCookie(refreshToken));
    response.setStatus(HttpServletResponse.SC_OK);
    log.info("[로그인성공 헤더] {}", response.getHeader(ACCESS_TOKEN.getKey()));
    log.info("[로그인성공 쿠키] {}", response.getHeader(COOKIE.getKey()));
  }

  private MemberDetails getMemberDetails(Authentication authResult) {
    String nickname = authResult.getName();
    String loginType = nickname.substring(nickname.indexOf('[') + 1, nickname.length() - 1);
    if (loginType.equals(GOOGLE.getKey())) {
      return MemberDetails.from((Oauth2MemberDetails) authResult.getPrincipal());
    }
    return (MemberDetails) authResult.getPrincipal();
  }

  protected String obtainFcmToken(HttpServletRequest request) {
    return request.getParameter(FCM_TOKEN_KEY);
  }

  private void saveFcmToken(Long memberId, HttpServletRequest request) {
    String fcmToken = obtainFcmToken(request);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    member.setFcmToken(fcmToken);
    memberRepository.save(member);
  }

  private String getMemberRole(MemberDetails details) {
    Iterator<? extends GrantedAuthority> iterator = details.getAuthorities().iterator();
    GrantedAuthority auth = iterator.next();
    return auth.getAuthority();
  }
}
