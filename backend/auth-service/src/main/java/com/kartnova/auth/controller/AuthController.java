package com.kartnova.auth.controller;

import com.kartnova.auth.dto.request.LoginRequest;
import com.kartnova.auth.dto.request.LogoutRequest;
import com.kartnova.auth.dto.request.RefreshTokenRequest;
import com.kartnova.auth.dto.request.RegisterRequest;
import com.kartnova.auth.dto.response.ApiResponse;
import com.kartnova.auth.dto.response.AuthResponse;
import com.kartnova.auth.dto.response.TokenValidationResponse;
import com.kartnova.auth.service.AuthService;
import com.kartnova.auth.util.AppConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(AppConstants.BASE_AUTH_API)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(AppConstants.REGISTER_API)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);

        return ApiResponse.<AuthResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message(AppConstants.MESSAGE_REGISTER_SUCCESS)
                .data(response)
                .build();
    }

    @PostMapping(AppConstants.LOGIN_API)
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ApiResponse.<AuthResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(AppConstants.MESSAGE_LOGIN_SUCCESS)
                .data(response)
                .build();
    }

    @PostMapping(AppConstants.REFRESH_API)
    public ApiResponse<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);

        return ApiResponse.<AuthResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(AppConstants.MESSAGE_TOKEN_REFRESH_SUCCESS)
                .data(response)
                .build();
    }

    @PostMapping(AppConstants.LOGOUT_API)
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);

        return ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(AppConstants.MESSAGE_LOGOUT_SUCCESS)
                .data(null)
                .build();
    }

    @GetMapping(AppConstants.VALIDATE_API)
    public ApiResponse<TokenValidationResponse> validateToken(
            @RequestHeader(AppConstants.AUTHORIZATION) String authorizationHeader
    ) {
        TokenValidationResponse response = authService.validateToken(authorizationHeader);

        return ApiResponse.<TokenValidationResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message(AppConstants.MESSAGE_TOKEN_VALID)
                .data(response)
                .build();
    }
}