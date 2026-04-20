package com.kartnova.auth.service.impl;

import com.kartnova.auth.dto.request.LoginRequest;
import com.kartnova.auth.dto.request.LogoutRequest;
import com.kartnova.auth.dto.request.RefreshTokenRequest;
import com.kartnova.auth.dto.request.RegisterRequest;
import com.kartnova.auth.dto.response.AuthResponse;
import com.kartnova.auth.dto.response.TokenValidationResponse;
import com.kartnova.auth.entity.AuthUser;
import com.kartnova.auth.entity.RefreshToken;
import com.kartnova.auth.entity.Role;
import com.kartnova.auth.entity.UserRole;
import com.kartnova.auth.enums.RoleName;
import com.kartnova.auth.enums.UserStatus;
import com.kartnova.auth.exception.InvalidCredentialsException;
import com.kartnova.auth.exception.ResourceAlreadyExistsException;
import com.kartnova.auth.exception.ResourceNotFoundException;
import com.kartnova.auth.exception.UnauthorizedException;
import com.kartnova.auth.repository.AuthUserRepository;
import com.kartnova.auth.repository.RefreshTokenRepository;
import com.kartnova.auth.repository.RoleRepository;
import com.kartnova.auth.repository.UserRoleRepository;
import com.kartnova.auth.security.JwtUtil;
import com.kartnova.auth.service.AuthService;
import com.kartnova.auth.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException(AppConstants.ERROR_EMAIL_ALREADY_EXISTS);
        }

        if (authUserRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException(AppConstants.ERROR_PHONE_ALREADY_EXISTS);
        }

        AuthUser authUser = AuthUser.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .accountNonLocked(true)
                .enabled(true)
                .build();

        AuthUser savedUser = authUserRepository.save(authUser);

        Role defaultRole = roleRepository.findByName(RoleName.valueOf(AppConstants.DEFAULT_CUSTOMER_ROLE))
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_ROLE_NOT_FOUND));

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(defaultRole)
                .build();
        userRoleRepository.save(userRole);

        List<String> roles = List.of(defaultRole.getName().name());

        String accessToken = jwtUtil.generateAccessToken(
                savedUser.getId(),
                savedUser.getEmail(),
                roles
        );

        String refreshTokenValue = jwtUtil.generateRefreshToken(
                savedUser.getId(),
                savedUser.getEmail()
        );

        RefreshToken refreshToken = RefreshToken.builder()
                .user(savedUser)
                .token(refreshTokenValue)
                .expiryAt(jwtUtil.getRefreshTokenExpiryDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType(AppConstants.TOKEN_TYPE)
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_USER_NOT_FOUND));

            List<String> roles = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(authority -> authority.replace(AppConstants.ROLE_PREFIX, ""))
                    .toList();

            String accessToken = jwtUtil.generateAccessToken(
                    authUser.getId(),
                    authUser.getEmail(),
                    roles
            );

            String refreshTokenValue = jwtUtil.generateRefreshToken(
                    authUser.getId(),
                    authUser.getEmail()
            );

            List<RefreshToken> activeTokens =
                    refreshTokenRepository.findByUser_IdAndRevokedFalse(authUser.getId());

            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
            }
            refreshTokenRepository.saveAll(activeTokens);

            RefreshToken refreshToken = RefreshToken.builder()
                    .user(authUser)
                    .token(refreshTokenValue)
                    .expiryAt(jwtUtil.getRefreshTokenExpiryDateTime())
                    .revoked(false)
                    .build();

            refreshTokenRepository.save(refreshToken);

            return AuthResponse.builder()
                    .userId(authUser.getId())
                    .email(authUser.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenValue)
                    .tokenType(AppConstants.TOKEN_TYPE)
                    .roles(roles)
                    .build();

        } catch (Exception ex) {
            throw new InvalidCredentialsException(AppConstants.ERROR_INVALID_CREDENTIALS);
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_REFRESH_TOKEN_NOT_FOUND));

        if (existingToken.isRevoked()) {
            throw new UnauthorizedException(AppConstants.ERROR_REFRESH_TOKEN_REVOKED);
        }

        if (!jwtUtil.isTokenValid(existingToken.getToken())) {
            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
            throw new UnauthorizedException(AppConstants.ERROR_REFRESH_TOKEN_EXPIRED);
        }

        AuthUser authUser = existingToken.getUser();

        List<String> roles = userRoleRepository.findByUser_Id(authUser.getId())
                .stream()
                .map(UserRole::getRole)
                .map(role -> role.getName().name())
                .toList();

        String newAccessToken = jwtUtil.generateAccessToken(
                authUser.getId(),
                authUser.getEmail(),
                roles
        );

        String newRefreshTokenValue = jwtUtil.generateRefreshToken(
                authUser.getId(),
                authUser.getEmail()
        );

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(authUser)
                .token(newRefreshTokenValue)
                .expiryAt(jwtUtil.getRefreshTokenExpiryDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        return AuthResponse.builder()
                .userId(authUser.getId())
                .email(authUser.getEmail())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenValue)
                .tokenType(AppConstants.TOKEN_TYPE)
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERROR_REFRESH_TOKEN_NOT_FOUND));

        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException(AppConstants.ERROR_REFRESH_TOKEN_REVOKED);
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenValidationResponse validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(AppConstants.BEARER)) {
            throw new UnauthorizedException(AppConstants.ERROR_MISSING_BEARER_TOKEN);
        }

        String token = authorizationHeader.substring(AppConstants.BEARER.length());

        if (!jwtUtil.isTokenValid(token)) {
            throw new UnauthorizedException(AppConstants.ERROR_INVALID_TOKEN);
        }

        return TokenValidationResponse.builder()
                .valid(true)
                .userId(jwtUtil.extractUserId(token))
                .email(jwtUtil.extractUsername(token))
                .roles(jwtUtil.extractRoles(token))
                .build();
    }
}