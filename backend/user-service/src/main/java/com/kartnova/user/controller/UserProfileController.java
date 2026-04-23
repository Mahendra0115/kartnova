package com.kartnova.user.controller;

import com.kartnova.user.constant.ApiPaths;
import com.kartnova.user.dto.common.ApiResponse;
import com.kartnova.user.dto.request.CreateUserProfileRequest;
import com.kartnova.user.dto.request.UpdateUserProfileRequest;
import com.kartnova.user.dto.response.UserProfileResponse;
import com.kartnova.user.service.UserProfileService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.API_BASE)
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping(ApiPaths.PROFILE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUserProfile(
            @Valid @RequestBody CreateUserProfileRequest request
    ) {
        UserProfileResponse response = userProfileService.createUserProfile(request);

        ApiResponse<UserProfileResponse> apiResponse = ApiResponse.<UserProfileResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("User profile created successfully")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping(ApiPaths.PROFILE_BY_ID)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfileById(
            @PathVariable Long profileId
    ) {
        UserProfileResponse response = userProfileService.getProfileById(profileId);

        ApiResponse<UserProfileResponse> apiResponse = ApiResponse.<UserProfileResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User profile fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(ApiPaths.PROFILE_BY_AUTH_USER_ID)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfileByAuthUserId(
            @PathVariable Long authUserId
    ) {
        UserProfileResponse response = userProfileService.getProfileByAuthUserId(authUserId);

        ApiResponse<UserProfileResponse> apiResponse = ApiResponse.<UserProfileResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User profile fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(ApiPaths.PROFILE_BY_ID)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        UserProfileResponse response = userProfileService.updateProfile(profileId, request);

        ApiResponse<UserProfileResponse> apiResponse = ApiResponse.<UserProfileResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User profile updated successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping(ApiPaths.PROFILE_DEACTIVATE)
    public ResponseEntity<ApiResponse<Void>> deactivateProfile(
            @PathVariable Long profileId
    ) {
        userProfileService.deactivateProfile(profileId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User profile deactivated successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}