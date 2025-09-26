package com.example.kspot.users.controller;

import com.example.kspot.contents.dto.ApiResponse;
import com.example.kspot.users.entity.Users;
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
        return ResponseEntity.ok(new ApiResponse(200,"사용자들 정보 조회 성공" , data));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUsersByNickname(@PathVariable long id) {
        var data = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse(200 , "사용자 정보 조회 성공" , data));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        var data = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    @PutMapping{"/{id}"}
    public ResponseEntity<ApiResponse> updateUser(@PathVariable long id, @RequestBody String nickname) {

    }

    @DeleteMapping{"/{id}"}
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {

    }


}
