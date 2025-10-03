package com.example.kspot.users.controller;

import com.example.kspot.contents.dto.ApiResponse;
import com.example.kspot.users.dto.UserInfoResponseDto;
import com.example.kspot.users.dto.UserRequestDto;
import com.example.kspot.users.dto.UserUpdataRequestDto;
import com.example.kspot.users.dto.UserUpdateResponseDto;
import com.example.kspot.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUsers() {

        var data = userService.getUsers();
        return ResponseEntity.ok(new ApiResponse<>(200,"사용자들 정보 조회 성공" , data));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getUsersById(@PathVariable long id) {
        UserInfoResponseDto data = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(200 , "사용자 정보 조회 성공" , data));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto user) {
        var data = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(204 , "회원가입 성공" , data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable long id, @RequestBody UserUpdataRequestDto nickname) {
        UserUpdateResponseDto data = userService.updateUser(id, nickname);
        System.out.println(data);
        return ResponseEntity.ok(new ApiResponse<>(200 , "사용자 이름 변경 성공" , data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserRequestDto dto) {

        var accessToken = userService.login(dto);
        return ResponseEntity.ok(new ApiResponse<>(200,"로그인 성공" , accessToken));
    }

}
