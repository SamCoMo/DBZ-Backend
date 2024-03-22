package com.samcomo.dbz.global.config;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.jwt.filter.AccessTokenFilter;
import com.samcomo.dbz.member.jwt.filter.FilterMemberExceptionHandler;
import com.samcomo.dbz.member.jwt.filter.LoginFilter;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final AuthenticationConfiguration configuration;
  private final RefreshTokenRepository refreshTokenRepository;

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration configuration) throws Exception {

    return configuration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        .csrf((auth) -> auth.disable())
        .formLogin((auth) -> auth.disable())
        .httpBasic((auth) -> auth.disable());

    // mapping
    http
        .authorizeHttpRequests((auth) -> auth
            .requestMatchers(
                "/member/register",
                "/member/login",
                "/member/reissue",
                "/docs/**",
                "/v3/api-docs/**",
                "/aop/",
                "/actuator/**"
            ).permitAll()
            // member
            .requestMatchers("/report/**").hasRole("MEMBER")
            // report
            .requestMatchers("/report/**").hasRole("MEMBER")
            // pin
            .requestMatchers(HttpMethod.POST, "/pin").hasRole("MEMBER") // Pin 생성
            .requestMatchers(HttpMethod.PUT, "/pin/{pinId}").hasRole("MEMBER") // Pin 수정
            .requestMatchers(HttpMethod.DELETE, "/pin/{pinId}").hasRole("MEMBER") // Pin 삭제
            .requestMatchers(HttpMethod.GET, "/pin/report/{reportId}/pin-list").hasRole("MEMBER") // Report 의 Pin List 가져오기
            .requestMatchers(HttpMethod.GET, "/pin/{pinId}").hasRole("MEMBER") // Pin 상세정보 가져오기
            // chat
            .requestMatchers(HttpMethod.POST, "/chat/room").hasRole("MEMBER") // 채팅방 생성
            .requestMatchers(HttpMethod.GET, "/chat/member/room-list").hasRole("MEMBER") // 회원 채팅방 목록 조회
            .requestMatchers(HttpMethod.GET, "/chat/room/{chatRoomId}/message-list").hasRole("MEMBER") // 채팅방 메시지 목록 조회
            .requestMatchers(HttpMethod.DELETE, "/chat/room/{chatRoomId}").hasRole("MEMBER") // 채팅방 삭제
            .anyRequest().authenticated());

    // session : stateless
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // filter
    http
        .addFilterBefore(new FilterMemberExceptionHandler(), LogoutFilter.class) // TODO : 커스텀 로그아웃 필터로 변경
        .addFilterBefore(
            new LoginFilter(authenticationManager(configuration), jwtUtil, refreshTokenRepository),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new AccessTokenFilter(jwtUtil), LoginFilter.class);

    return http.build();
  }
}
