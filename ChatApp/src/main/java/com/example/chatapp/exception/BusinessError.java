package com.example.chatapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessError {
    INCORRECT_CURRENT_PASSWORD("Podane hasło jest nieprawidłowe"),
    NEW_PASSWORD_DOES_NOT_MATCH("Nowe hasło nie pasuje"),
    ACCOUNT_DISABLED("Twoje konto jest nieaktywne, sprawdź swoją skrzynkę e-mail"),
    BAD_CREDENTIALS("Login i / lub hasło są nieprawidłowe"),
    EMAIL_NOT_FOUND("Nie znaleziono użytkownika z podanym adresem e-mail"),
    ;

    private final String description;
}
