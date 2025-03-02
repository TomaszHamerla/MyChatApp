package com.example.chatapp.controller;

import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/register")
    public void register(@RequestBody @Valid AuthReq authReq) {
        authService.register(authReq);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid AuthReq authReq) {
        return authService.login(authReq);
    }
}
