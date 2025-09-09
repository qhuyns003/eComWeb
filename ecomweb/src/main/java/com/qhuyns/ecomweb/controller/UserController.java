package com.qhuyns.ecomweb.controller;


import com.qhuyns.ecomweb.dto.request.*;
import com.qhuyns.ecomweb.dto.response.UserResponse;
import com.qhuyns.ecomweb.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @GetMapping("/{username}")
    ApiResponse<?> getUserIdbyUsername(@PathVariable String username){
        return ApiResponse.builder()
                .result(userService.getUserIdByUsername(username))
                .build();
    }

    @PutMapping("/toSeller")
    ApiResponse<?> upgradeToSeller(@RequestBody UpgradeSellerRequest request) throws Exception {
        return ApiResponse.builder()
                .result(userService.upgradeSellerRequest(request))
                .build();
    }

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) throws Exception {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/verification")
    ApiResponse<?> activeUser(@RequestParam String username, @RequestParam String token) {
        userService.activeUser(username, token);
        return ApiResponse.builder()
                .result("success")
                .build();
    }




    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PutMapping("/my-info")
    ApiResponse<UserResponse> updateUser( @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser( request))
                .build();
    }
}
