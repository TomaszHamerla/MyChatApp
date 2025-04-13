package com.example.chatapp.controller;

import com.example.chatapp.model.user.UserResponse;
import com.example.chatapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return userService.finAllUsersExceptSelf(jwt);
    }
}
