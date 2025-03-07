package com.example.chatapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessError {
    INCORRECT_CURRENT_PASSWORD("Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH("The new password does not match"),
    ACCOUNT_DISABLED("User account is disabled"),
    BAD_CREDENTIALS("Login and / or Password is incorrect"),
    ;

    private final String description;
}
