package com.example.kspot.users.controller;

import com.example.kspot.contents.dto.ApiResponseDto;
import com.example.kspot.users.dto.UserInfoResponseDto;
import com.example.kspot.users.dto.UserRequestDto;
import com.example.kspot.users.dto.UserUpdataRequestDto;
import com.example.kspot.users.dto.UserUpdateResponseDto;
import com.example.kspot.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="Users", description = "사용자 관련 API(회원가입, 로그인, 회원관리)")
@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Value("${jwt.access-ttl}")
    private long accessTtl;
    @Value("${jwt.refresh-ttl}")
    private long refreshTtl;

    private final UserService userService;
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    //1. 전체 사용자 조회
    @Operation(summary = "전체 사용자 조회", description = "등록된 모든 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 또는 누락된 토큰"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDto<?>> getUsers() {

        var data = userService.getUsers();
        return ResponseEntity.ok(new ApiResponseDto<>(200,"사용자들 정보 조회 성공" , data));

    }

    //2. 특정 사용자 조회
    @Operation(summary = "특정 사용자 조회", description = "사용자 ID를 통해 특정 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> getUsersById(@PathVariable long id) {
        UserInfoResponseDto data = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponseDto<>(200 , "사용자 정보 조회 성공" , data));
    }

    //3. 회원가입
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청 데이터 오류"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 사용자"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto user) {
        var token = userService.register(user);

        ResponseCookie refreshCookie = ResponseCookie.from("__Host-refresh_token", token.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTtl)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("__Host-access_token", token.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(accessTtl)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new ApiResponseDto<>(204 , "회원가입 성공" , token.accessToken()));
    }

    //4. 사용자 이름 수정
    @Operation(summary = "사용자 정보 수정", description = "사용자의 이름을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDto<?>> updateUser( @Parameter(description = "수정할 사용자 ID", example = "1")
                                                             @PathVariable long id, @RequestBody UserUpdataRequestDto nickname) {
        UserUpdateResponseDto data = userService.updateUser(id, nickname);
        System.out.println(data);
        return ResponseEntity.ok(new ApiResponseDto<>(200 , "사용자 이름 변경 성공" , data));
    }

    //5. 사용자 삭제
    @Operation(summary = "사용자 삭제", description = "특정 사용자를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공 (본문 없음)"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "삭제할 사용자 ID", example = "1")
                                               @PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //6.로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 이용해 로그인하고, Access Token을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 - 토큰 반환",
                    content = @Content(schema = @Schema(example = "{\"accessToken\": \"JWT_ACCESS_TOKEN_HERE\"}"))),
            @ApiResponse(responseCode = "400", description = "잘못된 로그인 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 비밀번호 불일치 또는 계정 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<?>> login(@RequestBody UserRequestDto dto) {

        var token = userService.login(dto);

        ResponseCookie refreshCookie = ResponseCookie.from("__Host-refresh_token", token.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(refreshTtl)
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("__Host-access_token", token.accessToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(accessTtl)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new ApiResponseDto<>(200, "로그인 성공", token.accessToken()));
    }

    @PostMapping("/masterToken")
    public ResponseEntity<ApiResponseDto<?>> getMasterToken() {
        var masterToken = userService.getMasterToken();
        return ResponseEntity.ok(new ApiResponseDto<>(204 , "master 토큰 생성 성공" , masterToken));
    }

}
