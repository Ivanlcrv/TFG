package com.example.app.ui.register;

import androidx.annotation.Nullable;

class RegisterFormState {
    @Nullable
    private final Integer usernameError;
    private final Integer passwordError;
    private final Integer emailError;
    private final Integer genreError;
    private final boolean isDataValid;

    RegisterFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer genreError, @Nullable Boolean isDataValid) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.emailError = emailError;
        this.genreError = genreError;
        this.isDataValid = Boolean.TRUE.equals(isDataValid);
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getGenreError() {
        return genreError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}
