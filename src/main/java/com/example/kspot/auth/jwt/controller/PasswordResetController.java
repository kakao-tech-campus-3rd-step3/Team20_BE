package com.example.kspot.auth.jwt.controller;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.email.dto.ResetPasswordDto;
import com.example.kspot.email.service.EmailVerificationService;
import com.example.kspot.users.dto.UserRequestDto;
import com.example.kspot.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password-reset")
public class PasswordResetController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @PatchMapping
    public ResponseEntity<ApiResponseDto<?>> resetPassword(@RequestBody UserRequestDto dto) {
        userService.resetPassword(dto);
        return ResponseEntity.ok(new ApiResponseDto<>(200 , "비밀번호 변경에 성공했습니다!" , null));
    }

    @PostMapping()
    public ResponseEntity<ApiResponseDto<?>> confirmResetPassword(@RequestBody ResetPasswordDto dto) {
        emailVerificationService.send(dto.email(),1);
        return ResponseEntity.ok(new ApiResponseDto<>(200, "비밀번호 변경을 위해 인증 메일을 확인해 주세요(이미 인증된 경우 무시)." , null ));
    }

}
