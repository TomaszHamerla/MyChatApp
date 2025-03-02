package com.example.chatapp.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthReq {
    @Email
    @NotBlank(message = "Email can not be empty")
    private String email;
    @Size(min = 5, message = "Required 5 characters for password")
    private String password;
}
