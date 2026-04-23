package com.kartnova.user.event.payload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAddressAddedEvent {

    private final Long addressId;
    private final Long profileId;
    private final Boolean isDefault;
}