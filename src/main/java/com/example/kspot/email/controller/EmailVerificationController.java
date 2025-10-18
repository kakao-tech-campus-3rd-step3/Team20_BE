package com.example.kspot.email.controller;


import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.email.dto.EmailResponseDto;
import com.example.kspot.email.service.EmailVerificationService;
import com.example.kspot.email.dto.ResetPasswordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Email Verification", description = "이메일 인증 관련 API")
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService verificationService;
    
    @Value("${jwt.access-ttl}")
    private long accessTtl;
    @Value("${jwt.refresh-ttl}")
    private long refreshTtl;

    @Operation(summary = "이메일 인증 확인", description = "토큰을 통해 이메일 인증을 완료합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized"),
            @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam("token") String token) {
        EmailResponseDto createdToken = verificationService.verifyByRawToken(token);

        ResponseCookie refreshToken = ResponseCookie.from("__Host-refresh_token", createdToken.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTtl)
                .build();

        ResponseCookie accessToken = ResponseCookie.from("__Host-access_token", createdToken.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(accessTtl)
                .build();

        return ResponseEntity.status(200)
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .body(new ApiResponseDto<>(200,"이메일 인증이 완료되었습니다.", accessToken.toString()));
    }

    @Operation(summary = "이메일 인증 요청", description = "입력한 이메일 주소로 인증 메일을 발송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", ref = "#/components/responses/Ok"),
            @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest"),
            @ApiResponse(responseCode = "500", ref = "#/components/responses/InternalServerError")
    })
    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestBody Map<String,String> body) {
        String email = body.getOrDefault("email", "");
        verificationService.send(email,0);
        return ResponseEntity.ok(Map.of("message", "인증 메일을 확인해 주세요(이미 인증된 경우 무시)."));
    }



}