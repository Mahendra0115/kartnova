package com.kartnova.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TokenValidationResponse {

    private boolean valid;
    private Long userId;
    private String email;
    private List<String> roles;
}