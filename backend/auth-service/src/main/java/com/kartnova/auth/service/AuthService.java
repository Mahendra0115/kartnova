package com.kartnova.auth.service;

import com.kartnova.auth.dto.request.LoginRequest;
import com.kartnova.auth.dto.request.LogoutRequest;
import com.kartnova.auth.dto.request.RefreshTokenRequest;
import com.kartnova.auth.dto.request.RegisterRequest;
import com.kartnova.auth.dto.response.AuthResponse;
import com.kartnova.auth.dto.response.TokenValidationResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    TokenValidationResponse validateToken(String authorizationHeader);
}