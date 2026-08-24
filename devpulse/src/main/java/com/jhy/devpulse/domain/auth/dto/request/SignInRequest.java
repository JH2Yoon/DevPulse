package com.jhy.devpulse.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignInRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}