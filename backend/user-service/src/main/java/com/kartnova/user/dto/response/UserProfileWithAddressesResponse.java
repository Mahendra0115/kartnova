package com.kartnova.user.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileWithAddressesResponse {

    private UserProfileResponse profile;
    private List<UserAddressResponse> addresses;
}