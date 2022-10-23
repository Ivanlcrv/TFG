package com.example.app.ui.register;

import androidx.annotation.Nullable;

public class RegisterResult {
    @Nullable
    private String username;
    @Nullable
    private Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    RegisterResult(@Nullable String username) {
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
