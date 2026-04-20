package com.kartnova.auth.config;

import com.kartnova.auth.entity.Role;
import com.kartnova.auth.enums.RoleName;
import com.kartnova.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRole(RoleName.CUSTOMER);
        seedRole(RoleName.ADMIN);
        seedRole(RoleName.SUPER_ADMIN);
    }

    private void seedRole(RoleName roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).build()
                ));
    }
}