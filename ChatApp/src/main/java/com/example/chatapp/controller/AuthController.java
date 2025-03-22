package com.example.chatapp.controller;

import com.example.chatapp.model.auth.AuthReq;
import com.example.chatapp.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
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
    public void register(@RequestBody @Valid AuthReq authReq, HttpServletRequest request) throws MessagingException {
        String url = request.getHeader("Referer");
        authService.register(authReq, url);
    }

    @PostMapping("/login")
    public String login(@RequestBody @Valid AuthReq authReq) {
        return authService.login(authReq);
    }

    @PostMapping("/activate-account")
    public void confirm(
            @RequestParam String token, HttpServletRequest request
    ) throws MessagingException {
        String url = request.getHeader("Referer");
        authService.activateAccount(token, url);
    }
}
