package com.kartnova.auth.repository;

import com.kartnova.auth.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);
    Optional<AuthUser> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}