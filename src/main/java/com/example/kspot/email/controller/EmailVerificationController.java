package com.example.kspot.email.controller;


import com.example.kspot.email.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Email Verification", description = "이메일 인증 관련 API")
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService verificationService;

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
        verificationService.verifyByRawToken(token);
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
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
        verificationService.resend(email);
        return ResponseEntity.ok(Map.of("message", "인증 메일을 확인해 주세요(이미 인증된 경우 무시)."));
    }
}