package com.kartnova.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {

    private Long userId;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private List<String> roles;
}