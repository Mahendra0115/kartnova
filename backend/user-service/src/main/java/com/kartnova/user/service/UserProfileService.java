package com.kartnova.user.service;

import com.kartnova.user.dto.request.CreateUserProfileRequest;
import com.kartnova.user.dto.request.UpdateUserProfileRequest;
import com.kartnova.user.dto.response.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse createUserProfile(CreateUserProfileRequest request);

    UserProfileResponse getProfileById(Long profileId);

    UserProfileResponse getProfileByAuthUserId(Long authUserId);

    UserProfileResponse updateProfile(Long profileId, UpdateUserProfileRequest request);

    void deactivateProfile(Long profileId);
}