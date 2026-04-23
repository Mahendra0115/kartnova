package com.kartnova.user.controller;

import com.kartnova.user.constant.ApiPaths;
import com.kartnova.user.dto.common.ApiResponse;
import com.kartnova.user.dto.request.CreateAddressRequest;
import com.kartnova.user.dto.request.UpdateAddressRequest;
import com.kartnova.user.dto.response.UserAddressResponse;
import com.kartnova.user.service.UserAddressService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.API_BASE)
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @PostMapping(ApiPaths.ADDRESSES)
    public ResponseEntity<ApiResponse<UserAddressResponse>> addAddress(
            @PathVariable Long profileId,
            @Valid @RequestBody CreateAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.addAddress(profileId, request);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("User address added successfully")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping(ApiPaths.ADDRESSES)
    public ResponseEntity<ApiResponse<List<UserAddressResponse>>> getAllAddresses(
            @PathVariable Long profileId
    ) {
        List<UserAddressResponse> response = userAddressService.getAllAddresses(profileId);

        ApiResponse<List<UserAddressResponse>> apiResponse = ApiResponse.<List<UserAddressResponse>>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User addresses fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(ApiPaths.ADDRESS_BY_ID)
    public ResponseEntity<ApiResponse<UserAddressResponse>> getAddressById(
            @PathVariable Long profileId,
            @PathVariable Long addressId
    ) {
        UserAddressResponse response = userAddressService.getAddressById(profileId, addressId);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User address fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(ApiPaths.ADDRESS_BY_ID)
    public ResponseEntity<ApiResponse<UserAddressResponse>> updateAddress(
            @PathVariable Long profileId,
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request
    ) {
        UserAddressResponse response = userAddressService.updateAddress(profileId, addressId, request);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User address updated successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping(ApiPaths.ADDRESS_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long profileId,
            @PathVariable Long addressId
    ) {
        userAddressService.deleteAddress(profileId, addressId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User address deleted successfully")
                .data(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping(ApiPaths.SET_DEFAULT_ADDRESS)
    public ResponseEntity<ApiResponse<UserAddressResponse>> setDefaultAddress(
            @PathVariable Long profileId,
            @PathVariable Long addressId
    ) {
        UserAddressResponse response = userAddressService.setDefaultAddress(profileId, addressId);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Default address updated successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(ApiPaths.DEFAULT_ADDRESS)
    public ResponseEntity<ApiResponse<UserAddressResponse>> getDefaultAddress(
            @PathVariable Long profileId
    ) {
        UserAddressResponse response = userAddressService.getDefaultAddress(profileId);

        ApiResponse<UserAddressResponse> apiResponse = ApiResponse.<UserAddressResponse>builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Default address fetched successfully")
                .data(response)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}