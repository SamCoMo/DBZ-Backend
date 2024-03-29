package com.samcomo.dbz.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "동반자 DBZ",
        description = "동네 반려동물 찾자",
        version = "0.1v"))
@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI openAPI(){
    final String SECURITY_SCHEME_KEY = "accessAuth"; // 보안 스키마 키를 정의
    final String JWT_HEADER = "Access-Token";

    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER).name(JWT_HEADER);
    SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_KEY);

    return new OpenAPI()
        .components(new Components().addSecuritySchemes(SECURITY_SCHEME_KEY, securityScheme))
        .security(Arrays.asList(securityRequirement));
  }
}

