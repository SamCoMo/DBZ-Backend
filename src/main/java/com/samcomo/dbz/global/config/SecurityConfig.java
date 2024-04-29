package com.samcomo.dbz.global.config;

import com.samcomo.dbz.member.jwt.AuthUtils;
import com.samcomo.dbz.member.jwt.CookieUtil;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.jwt.filter.AccessTokenFilter;
import com.samcomo.dbz.member.jwt.filter.CustomLoginFilter;
import com.samcomo.dbz.member.jwt.filter.CustomLogoutFilter;
import com.samcomo.dbz.member.jwt.filter.handler.FilterMemberExceptionHandler;
import com.samcomo.dbz.member.jwt.filter.handler.LoginSuccessHandler;
import com.samcomo.dbz.member.jwt.filter.handler.Oauth2LoginFailureHandler;
import com.samcomo.dbz.member.service.impl.Oauth2MemberServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final CookieUtil cookieUtil;
  private final AuthenticationConfiguration configuration;
  private final LoginSuccessHandler loginSuccessHandler;
  private final Oauth2LoginFailureHandler oauth2LoginFailureHandler;
  private final Oauth2MemberServiceImpl oauth2MemberService;

  private final static String MEMBER = "MEMBER";

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public AuthUtils getAuthUtils() {
    return AuthUtils.of(jwtUtil, cookieUtil);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .cors((cors) -> cors
            .configurationSource(new CorsConfigurationSource() {
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);
                configuration.setExposedHeaders(Collections.singletonList("Access-Token"));

                return configuration;
              }
            }));

    http
        .csrf((auth) -> auth.disable())
        .formLogin((auth) -> auth.disable())
        .httpBasic((auth) -> auth.disable());

    // mapping
    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(
                "/oauth2/authorization/**", // 소셜 로그인 버튼
                "/login/oauth2/code/**", // 소셜 로그인 콜백
                "/member/register", // 회원가입
                "/member/login", // 기본 로그인
                "/member/reissue", // access 토큰 재발급
                "/docs/**",
                "/v3/api-docs/**",
                "/aop/",
                "/actuator/**",
                "/ws/**",
                "/chat/room"
            ).permitAll()
            // member
            .requestMatchers(GET, "/member/my").hasRole(MEMBER) // 마이페이지
            .requestMatchers(PATCH, "/member/location").hasRole(MEMBER) // 회원 위치 업데이트
            .requestMatchers(PATCH, "/member/profile-image").hasRole(MEMBER) // 프로필 이미지 업데이트
            // report
            .requestMatchers("/report/**").hasRole(MEMBER)
            // pin
            .requestMatchers(POST, "/pin").hasRole(MEMBER) // Pin 생성
            .requestMatchers(PUT, "/pin/{pinId}").hasRole(MEMBER) // Pin 수정
            .requestMatchers(DELETE, "/pin/{pinId}").hasRole(MEMBER) // Pin 삭제
            .requestMatchers(GET, "/pin/report/{reportId}/pin-list").hasRole(MEMBER) // Report 의 Pin List 가져오기
            .requestMatchers(GET, "/pin/{pinId}").hasRole(MEMBER) // Pin 상세정보 가져오기
            // chat
            .requestMatchers("/ws").hasRole(MEMBER) // 웹소켓 접근
            .requestMatchers(POST, "/chat/room").hasRole(MEMBER) // 채팅방 생성
            .requestMatchers(GET, "/chat/member/room-list").hasRole(MEMBER) // 회원 채팅방 목록 조회
            .requestMatchers(GET, "/chat/room/{chatRoomId}/message-list").hasRole(MEMBER) // 채팅방 메시지 목록 조회
            .requestMatchers(DELETE, "/chat/room/{chatRoomId}").hasRole(MEMBER) // 채팅방 삭제
            // notification
            .requestMatchers(GET, "/notification/list").hasRole(MEMBER) // 알림 목록 조회
            .anyRequest().authenticated());

    // oauth2 social login
    http
        .oauth2Login((oauth2) -> oauth2
            .userInfoEndpoint((config) -> config
                .userService(oauth2MemberService))
            .successHandler(loginSuccessHandler)
            .failureHandler(oauth2LoginFailureHandler));

    // session : stateless
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // filter
    http
        .addFilterBefore(
            new CustomLogoutFilter(getAuthUtils()), LogoutFilter.class)
        .addFilterBefore(
            new FilterMemberExceptionHandler(), CustomLogoutFilter.class)
        .addFilterBefore(
            new CustomLoginFilter(authenticationManager(configuration), loginSuccessHandler), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            new AccessTokenFilter(jwtUtil), CustomLoginFilter.class);

    return http.build();
  }
}
