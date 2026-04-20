package com.kartnova.auth.repository;

import com.kartnova.auth.entity.Role;
import com.kartnova.auth.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}