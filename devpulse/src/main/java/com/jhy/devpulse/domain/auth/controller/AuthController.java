package com.jhy.devpulse.domain.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhy.devpulse.domain.auth.dto.request.SignUpRequest;
import com.jhy.devpulse.domain.auth.dto.request.SignInRequest;
import com.jhy.devpulse.domain.auth.dto.response.TokenResponse;
import com.jhy.devpulse.domain.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
        TokenResponse tokenResponse = authService.signIn(request);

        return ResponseEntity.ok(tokenResponse);
    }
}
