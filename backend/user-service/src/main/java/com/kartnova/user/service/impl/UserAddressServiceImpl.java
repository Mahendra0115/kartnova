package com.kartnova.user.service.impl;

import com.kartnova.user.constant.AppConstants;
import com.kartnova.user.dto.request.CreateAddressRequest;
import com.kartnova.user.dto.request.UpdateAddressRequest;
import com.kartnova.user.dto.response.UserAddressResponse;
import com.kartnova.user.entity.UserAddress;
import com.kartnova.user.entity.UserProfile;
import com.kartnova.user.enums.AccountStatus;
import com.kartnova.user.exception.DefaultAddressNotFoundException;
import com.kartnova.user.event.payload.UserAddressAddedEvent;
import com.kartnova.user.event.producer.UserProfileEventProducer;
import com.kartnova.user.exception.UserAddressNotFoundException;
import com.kartnova.user.exception.UserProfileNotFoundException;
import com.kartnova.user.mapper.UserAddressMapper;
import com.kartnova.user.repository.UserAddressRepository;
import com.kartnova.user.repository.UserProfileRepository;
import com.kartnova.user.service.UserAddressService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserAddressMapper userAddressMapper;
    private final UserProfileEventProducer userProfileEventProducer;

    @Override
    public UserAddressResponse addAddress(Long profileId, CreateAddressRequest request) {
        UserProfile userProfile = getActiveUserProfile(profileId);

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            request.setCountry(AppConstants.DEFAULT_COUNTRY);
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearExistingDefaultAddress(profileId);
        }

        UserAddress userAddress = userAddressMapper.toEntity(request);
        userAddress.setUserProfile(userProfile);
        userAddress.setIsActive(Boolean.TRUE);
        userAddress.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        UserAddress savedUserAddress = userAddressRepository.save(userAddress);

        
        
        userProfileEventProducer.publishUserAddressAdded(
                UserAddressAddedEvent.builder()
                        .addressId(savedUserAddress.getId())
                        .profileId(profileId)
                        .isDefault(savedUserAddress.getIsDefault())
                        .build()
        );
        
        
//        userProfileEventProducer.publishUserAddressAdded(
//                UserAddressAddedEvent.builder()
//                        .addressId(savedUserAddress.getId())
//                        .profileId(profileId)
//                        .isDefault(savedUserAddress.getIsDefault())
//                        .build()
//        );

        return userAddressMapper.toResponse(savedUserAddress);
    }
    
    
    
    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAllAddresses(Long profileId) {
        getActiveUserProfile(profileId);

        return userAddressRepository.findAllByUserProfileIdAndIsActiveTrue(profileId)
                .stream()
                .map(userAddressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getAddressById(Long profileId, Long addressId) {
        getActiveUserProfile(profileId);

        UserAddress userAddress = userAddressRepository.findByIdAndUserProfileId(addressId, profileId)
                .filter(address -> Boolean.TRUE.equals(address.getIsActive()))
                .orElseThrow(() -> new UserAddressNotFoundException(
                        "User address not found for profile id: " + profileId + " and address id: " + addressId
                ));

        return userAddressMapper.toResponse(userAddress);
    }

    @Override
    public UserAddressResponse updateAddress(Long profileId, Long addressId, UpdateAddressRequest request) {
        getActiveUserProfile(profileId);

        UserAddress userAddress = userAddressRepository.findByIdAndUserProfileId(addressId, profileId)
                .filter(address -> Boolean.TRUE.equals(address.getIsActive()))
                .orElseThrow(() -> new UserAddressNotFoundException(
                        "User address not found for profile id: " + profileId + " and address id: " + addressId
                ));

        if (request.getCountry() == null || request.getCountry().isBlank()) {
            request.setCountry(AppConstants.DEFAULT_COUNTRY);
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(userAddress.getIsDefault())) {
            clearExistingDefaultAddress(profileId);
        }

        userAddress.setFullName(request.getFullName());
        userAddress.setPhoneNumber(request.getPhoneNumber());
        userAddress.setAddressLine1(request.getAddressLine1());
        userAddress.setAddressLine2(request.getAddressLine2());
        userAddress.setLandmark(request.getLandmark());
        userAddress.setCity(request.getCity());
        userAddress.setState(request.getState());
        userAddress.setPostalCode(request.getPostalCode());
        userAddress.setCountry(request.getCountry());
        userAddress.setAddressType(request.getAddressType());
        userAddress.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));

        UserAddress updatedUserAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toResponse(updatedUserAddress);
    }

    @Override
    public void deleteAddress(Long profileId, Long addressId) {
        getActiveUserProfile(profileId);

        UserAddress userAddress = userAddressRepository.findByIdAndUserProfileId(addressId, profileId)
                .filter(address -> Boolean.TRUE.equals(address.getIsActive()))
                .orElseThrow(() -> new UserAddressNotFoundException(
                        "User address not found for profile id: " + profileId + " and address id: " + addressId
                ));

        userAddress.setIsActive(Boolean.FALSE);
        userAddress.setIsDefault(Boolean.FALSE);
        userAddressRepository.save(userAddress);
    }

    @Override
    public UserAddressResponse setDefaultAddress(Long profileId, Long addressId) {
        getActiveUserProfile(profileId);

        UserAddress userAddress = userAddressRepository.findByIdAndUserProfileId(addressId, profileId)
                .filter(address -> Boolean.TRUE.equals(address.getIsActive()))
                .orElseThrow(() -> new UserAddressNotFoundException(
                        "User address not found for profile id: " + profileId + " and address id: " + addressId
                ));

        clearExistingDefaultAddress(profileId);
        userAddress.setIsDefault(Boolean.TRUE);

        UserAddress updatedUserAddress = userAddressRepository.save(userAddress);
        return userAddressMapper.toResponse(updatedUserAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getDefaultAddress(Long profileId) {
        getActiveUserProfile(profileId);

        UserAddress userAddress = userAddressRepository.findByUserProfileIdAndIsDefaultTrueAndIsActiveTrue(profileId)
                .orElseThrow(() -> new DefaultAddressNotFoundException(
                        "Default address not found for profile id: " + profileId
                ));

        return userAddressMapper.toResponse(userAddress);
    }

    private UserProfile getActiveUserProfile(Long profileId) {
        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for id: " + profileId));

        if (userProfile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserProfileNotFoundException("Active user profile not found for id: " + profileId);
        }

        return userProfile;
    }

    private void clearExistingDefaultAddress(Long profileId) {
        userAddressRepository.findByUserProfileIdAndIsDefaultTrueAndIsActiveTrue(profileId)
                .ifPresent(existingDefaultAddress -> {
                    existingDefaultAddress.setIsDefault(Boolean.FALSE);
                    userAddressRepository.save(existingDefaultAddress);
                });
    }
}