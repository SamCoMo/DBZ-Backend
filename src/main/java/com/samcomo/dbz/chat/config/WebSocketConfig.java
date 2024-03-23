package com.samcomo.dbz.chat.config;

import static com.samcomo.dbz.member.model.constants.TokenType.ACCESS_TOKEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcomo.dbz.member.jwt.JwtUtil;
import com.samcomo.dbz.member.model.dto.MemberDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 기반 MessageBroker 활성화
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtUtil jwtUtil;

  // 메시지 브로커 구성 ( 메시지 보낼때 사용하는 경로 설정 )
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // "/chatapp 경로로 통해 들어오는 메시지 -> 애플레케이션 내부로 라우팅되어서 처리
    registry.setApplicationDestinationPrefixes("/chatapp")
        // "/chatrooms" 메시지 브로커 활성화
        .enableSimpleBroker("/chatrooms"); // 채팅방별 메시지 브로커 활성화
  }

  // STOMP 프로토콜 사용시 WebSocket 엔드포인트 : "/ws"
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  // 메시지 변환기 구성 및 메시지 형식 설정
  @Override
  public boolean configureMessageConverters(
      List<MessageConverter> messageConverters) {
    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(APPLICATION_JSON);
    // JSON -> 자동 매핑 -> 객체가 전달
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setObjectMapper(new ObjectMapper());
    converter.setContentTypeResolver(resolver);
    messageConverters.add(new StringMessageConverter());
    messageConverters.add(new ByteArrayMessageConverter());
    messageConverters.add(converter);
    return true;
  }

  // client -> 들어오는 메시지 처리 인터셉터 구성
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new ChannelInterceptor() {
      // 메시지가 전송되기 전에 호출 -> 사용자 인증처리
      @Override
      public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
            message, StompHeaderAccessor.class);
        try{
          if (isConnectionRequest(accessor)) {
            String accessToken = getAccessToken(accessor);
            MemberDetails memberDetails = jwtUtil.extractMemberDetailsFrom(accessToken);

            // Authentication 생성 후 Security Context 에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                memberDetails, null, memberDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (Exception e){
          log.error("WebSocket Auth Failed: {}", e.getMessage());
          return null;
        }
        return message;
      }
    });
  }

  private boolean isConnectionRequest(StompHeaderAccessor accessor) {
    if (accessor == null) {
      return false;
    }
    // 클라이언트가 연결 시도
    return StompCommand.CONNECT.equals(accessor.getCommand());
  }

  private String getAccessToken(StompHeaderAccessor accessor) {
    return accessor.getFirstNativeHeader(ACCESS_TOKEN.getKey());
  }
}