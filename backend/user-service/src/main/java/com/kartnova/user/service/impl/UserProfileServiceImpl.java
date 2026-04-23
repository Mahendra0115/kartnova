package com.kartnova.user.service.impl;

import com.kartnova.user.dto.request.CreateUserProfileRequest;
import com.kartnova.user.dto.request.UpdateUserProfileRequest;
import com.kartnova.user.dto.response.UserProfileResponse;
import com.kartnova.user.entity.UserProfile;
import com.kartnova.user.enums.AccountStatus;
import com.kartnova.user.exception.UserProfileAlreadyExistsException;
import com.kartnova.user.event.payload.UserProfileCreatedEvent;
import com.kartnova.user.event.producer.UserProfileEventProducer;
import com.kartnova.user.exception.UserProfileNotFoundException;
import com.kartnova.user.mapper.UserProfileMapper;
import com.kartnova.user.repository.UserProfileRepository;
import com.kartnova.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileEventProducer userProfileEventProducer;

    
    @Override
    public UserProfileResponse createUserProfile(CreateUserProfileRequest request) {
        if (userProfileRepository.existsByAuthUserId(request.getAuthUserId())) {
            throw new UserProfileAlreadyExistsException("User profile already exists for auth user id: " + request.getAuthUserId());
        }

        if (userProfileRepository.existsByEmail(request.getEmail())) {
            throw new UserProfileAlreadyExistsException("User profile already exists for email: " + request.getEmail());
        }

        UserProfile userProfile = userProfileMapper.toEntity(request);
        userProfile.setAccountStatus(AccountStatus.ACTIVE);

        UserProfile savedUserProfile = userProfileRepository.save(userProfile);

        userProfileEventProducer.publishUserProfileCreated(
                UserProfileCreatedEvent.builder()
                        .profileId(savedUserProfile.getId())
                        .authUserId(savedUserProfile.getAuthUserId())
                        .email(savedUserProfile.getEmail())
                        .build()
        );

        return userProfileMapper.toResponse(savedUserProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileById(Long profileId) {
        UserProfile userProfile = getActiveUserProfileById(profileId);
        return userProfileMapper.toResponse(userProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByAuthUserId(Long authUserId) {
        UserProfile userProfile = userProfileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for auth user id: " + authUserId));

        if (userProfile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserProfileNotFoundException("Active user profile not found for auth user id: " + authUserId);
        }

        return userProfileMapper.toResponse(userProfile);
    }

    @Override
    public UserProfileResponse updateProfile(Long profileId, UpdateUserProfileRequest request) {
        UserProfile userProfile = getActiveUserProfileById(profileId);

        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfile.setPhoneNumber(request.getPhoneNumber());

        UserProfile updatedUserProfile = userProfileRepository.save(userProfile);
        return userProfileMapper.toResponse(updatedUserProfile);
    }

    @Override
    public void deactivateProfile(Long profileId) {
        UserProfile userProfile = getActiveUserProfileById(profileId);
        userProfile.setAccountStatus(AccountStatus.INACTIVE);
        userProfileRepository.save(userProfile);
    }

    private UserProfile getActiveUserProfileById(Long profileId) {
        UserProfile userProfile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new UserProfileNotFoundException("User profile not found for id: " + profileId));

        if (userProfile.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new UserProfileNotFoundException("Active user profile not found for id: " + profileId);
        }

        return userProfile;
    }
}