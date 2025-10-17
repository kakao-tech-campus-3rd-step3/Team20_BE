package com.example.kspot.config;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI kspotOpenAPI() {
        //JWT Security 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        Server server = new Server()
            .url("${kspot.server.url}")
            .description("K-SPOT HTTPS 서버");

        //공통 응답 스키마
        Components components = new Components()
                .addResponses("Ok",
                        new ApiResponse()
                                .description("정상 응답")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))))
                .addResponses("BadRequest",
                        new ApiResponse()
                                .description("잘못된 요청 (파라미터나 입력 값 오류)")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))))
                .addResponses("Unauthorized",
                        new ApiResponse()
                                .description("인증 실패 또는 토큰 만료")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))))
                .addResponses("NotFound",
                        new ApiResponse()
                                .description("리소스를 찾을 수 없음")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))))
                .addResponses("Conflict",
                        new ApiResponse()
                                .description("이미 사용된 리소스 (중복 요청)")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))))
                .addResponses("InternalServerError",
                        new ApiResponse()
                                .description("서버 내부 오류")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiResponseDto")))));

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("K-SPOT API 명세서")
                        .description("K-SPOT API 문서입니다.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
