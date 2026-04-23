package com.kartnova.user.repository;

import com.kartnova.user.entity.UserProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByAuthUserId(Long authUserId);

    boolean existsByAuthUserId(Long authUserId);

    boolean existsByEmail(String email);
}