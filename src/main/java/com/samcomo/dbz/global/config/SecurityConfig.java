package com.samcomo.dbz.global.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.jwt.filter.AccessTokenFilter;
import com.samcomo.dbz.member.jwt.filter.CustomLoginFilter;
import com.samcomo.dbz.member.jwt.filter.CustomLogoutFilter;
import com.samcomo.dbz.member.jwt.filter.FilterMemberExceptionHandler;
import com.samcomo.dbz.member.model.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final AuthenticationConfiguration configuration;
  private final MemberRepository memberRepository;

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
        .cors((cors) -> cors
            .configurationSource(new CorsConfigurationSource() {
              @Override
              public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(Collections.singletonList("*"));
                configuration.setAllowedMethods(Collections.singletonList("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedHeaders(Collections.singletonList("*"));
                configuration.setMaxAge(3600L);

                configuration.setExposedHeaders(Collections.singletonList("*"));

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
                "/member/register", // 회원가입
                "/member/login", // 로그인
                "/member/reissue", // accessToken 재발급
                "/docs/**",
                "/v3/api-docs/**",
                "/aop/",
                "/actuator/**"
            ).permitAll()
            // member
            .requestMatchers(GET, "/member/my").hasRole("MEMBER") // 마이페이지
            .requestMatchers(PATCH, "/member/location").hasRole("MEMBER") // 위치 정보 업데이트
            // report
            .requestMatchers("/report/**").hasRole("MEMBER")
            // pin
            .requestMatchers(POST, "/pin").hasRole("MEMBER") // Pin 생성
            .requestMatchers(PUT, "/pin/{pinId}").hasRole("MEMBER") // Pin 수정
            .requestMatchers(DELETE, "/pin/{pinId}").hasRole("MEMBER") // Pin 삭제
            .requestMatchers(GET, "/pin/report/{reportId}/pin-list").hasRole("MEMBER") // Report 의 Pin List 가져오기
            .requestMatchers(GET, "/pin/{pinId}").hasRole("MEMBER") // Pin 상세정보 가져오기
            // chat
            .requestMatchers("/ws").hasRole("MEMBER")
            .requestMatchers(POST, "/chat/room").hasRole("MEMBER") // 채팅방 생성
            .requestMatchers(GET, "/chat/member/room-list").hasRole("MEMBER") // 회원 채팅방 목록 조회
            .requestMatchers(GET, "/chat/room/{chatRoomId}/message-list").hasRole("MEMBER") // 채팅방 메시지 목록 조회
            .requestMatchers(DELETE, "/chat/room/{chatRoomId}").hasRole("MEMBER") // 채팅방 삭제
            // notificaiton
            .requestMatchers(GET, "/notification/list").hasRole("MEMBER") // 알림 목록 조회
            .anyRequest().authenticated());

    // session : stateless
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // filter
    http
        .addFilterBefore(
            new CustomLogoutFilter(jwtUtil), LogoutFilter.class)
        .addFilterBefore(
            new FilterMemberExceptionHandler(), CustomLogoutFilter.class)
        .addFilterBefore(
            new CustomLoginFilter(authenticationManager(configuration), jwtUtil, memberRepository), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(
            new AccessTokenFilter(jwtUtil), CustomLoginFilter.class);

    return http.build();
  }
}
