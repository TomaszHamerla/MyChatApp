package com.example.chatapp.model.urlRoute;

import lombok.Getter;

@Getter
public enum UrlRoute {
    ACTIVE_ACCOUNT("activate-account"),
    NEW_PASSWORD("new-password");

    private final String name;

    UrlRoute(String name) {
        this.name = name;
    }
}
