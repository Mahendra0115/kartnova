package com.kartnova.user.mapper;

import com.kartnova.user.dto.request.CreateUserProfileRequest;
import com.kartnova.user.dto.response.UserProfileResponse;
import com.kartnova.user.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserProfile toEntity(CreateUserProfileRequest request);

    UserProfileResponse toResponse(UserProfile userProfile);
}