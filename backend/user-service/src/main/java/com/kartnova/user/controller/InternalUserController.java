package com.kartnova.user.controller;

import com.kartnova.user.constant.ApiPaths;
import com.kartnova.user.dto.common.ApiResponse;
import com.kartnova.user.dto.response.UserAddressResponse;
import com.kartnova.user.dto.response.UserProfileResponse;
import com.kartnova.user.service.UserAddressService;
import com.kartnova.user.service.UserProfileService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.API_BASE)
@RequiredArgsConstructor
public class InternalUserController {

    private final UserProfileService userProfileService;
    private final UserAddressService userAddressService;

    @GetMapping(ApiPaths.INTERNAL_PROFILE_BY_AUTH_USER_ID)
    public ResponseEntity<ApiResponse<UserProfileResponse>> getInternalProfileByAuthUserId(
            @PathVariable Long authUserId
    ) {
        UserProfileResponse response = userProfileService.getProfileByAuthUserId(authUserId);

        ApiResponse<UserProfileResponse> apiResponse = ApiResponse.<UserProfileResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Internal user profile fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(ApiPaths.INTERNAL_DEFAULT_ADDRESS)
    public ResponseEntity<ApiResponse<UserAddressResponse>> getInternalDefaultAddress(
            @PathVariable Long profileId
    ) {
        UserAddressResponse response = userAddressService.getDefaultAddress(profileId);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Internal default address fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}