package com.example.app.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private String username;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable String username) {
        this.username = username;
    }

    @Nullable
    Integer getError() {
        return error;
    }

    @Nullable
    String getUserName() {
        return username;
    }
}