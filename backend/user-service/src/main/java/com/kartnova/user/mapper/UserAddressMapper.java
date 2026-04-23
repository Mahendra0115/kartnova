package com.kartnova.user.mapper;

import com.kartnova.user.dto.request.CreateAddressRequest;
import com.kartnova.user.dto.response.UserAddressResponse;
import com.kartnova.user.entity.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserAddress toEntity(CreateAddressRequest request);

    UserAddressResponse toResponse(UserAddress userAddress);
}