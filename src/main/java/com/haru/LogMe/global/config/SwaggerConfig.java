package com.haru.LogMe.global.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("LogMe API")
                .description("LogMe API 명세서")
                .version("v1.0.0");

        // 1. "bearer" 인증 방식을 SecurityScheme으로 정의
        String jwt = "JWT";
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT") // JWT 포맷
                .in(SecurityScheme.In.HEADER)
                .name("Authorization"); // 헤더 이름

        // 2. API 요청 시 "Bearer" 인증을 사용하도록 SecurityRequirement 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(securityRequirement);
    }
}
