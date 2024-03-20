package com.samcomo.dbz.global.config;

import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.jwt.filter.AccessTokenFilter;
import com.samcomo.dbz.member.jwt.filter.LoginFilter;
import com.samcomo.dbz.member.model.repository.RefreshTokenRepository;
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
                "/aop/"
            ).permitAll()
            .requestMatchers("/report/**", "/member/**").hasRole("MEMBER")
            .anyRequest().authenticated());

    // session : stateless
    http
        .sessionManagement((session) -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // filter
    http
        .addFilterBefore(
            new LoginFilter(authenticationManager(configuration), jwtUtil, refreshTokenRepository),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new AccessTokenFilter(jwtUtil), LoginFilter.class);

    return http.build();
  }
}
