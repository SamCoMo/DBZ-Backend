package com.samcomo.dbz.chat.config;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
// WebSocket 기반 MessageBroker 활성화
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker (MessageBrokerRegistry registry){
    // "/chatapp 경로로 통해 들어오는 메시지 -> 애플레케이션 내부로 라우팅되어서 처리
    registry.setApplicationDestinationPrefixes("/chatapp")
        // "/chatrooms" 메시지 브로커 활성화
        .enableSimpleBroker("/chatrooms"); // 채팅방별 메시지 브로커 활성화
  }


  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry){
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

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
}