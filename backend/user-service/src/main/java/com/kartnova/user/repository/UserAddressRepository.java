package com.kartnova.user.repository;

import com.kartnova.user.entity.UserAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    List<UserAddress> findAllByUserProfileIdAndIsActiveTrue(Long userProfileId);

    Optional<UserAddress> findByIdAndUserProfileId(Long addressId, Long userProfileId);

    Optional<UserAddress> findByUserProfileIdAndIsDefaultTrueAndIsActiveTrue(Long userProfileId);

    long countByUserProfileIdAndIsActiveTrue(Long userProfileId);
}