package com.kartnova.user.service;

import com.kartnova.user.dto.request.CreateAddressRequest;
import com.kartnova.user.dto.request.UpdateAddressRequest;
import com.kartnova.user.dto.response.UserAddressResponse;
import java.util.List;

public interface UserAddressService {

    UserAddressResponse addAddress(Long profileId, CreateAddressRequest request);

    List<UserAddressResponse> getAllAddresses(Long profileId);

    UserAddressResponse getAddressById(Long profileId, Long addressId);

    UserAddressResponse updateAddress(Long profileId, Long addressId, UpdateAddressRequest request);

    void deleteAddress(Long profileId, Long addressId);

    UserAddressResponse setDefaultAddress(Long profileId, Long addressId);

    UserAddressResponse getDefaultAddress(Long profileId);
}