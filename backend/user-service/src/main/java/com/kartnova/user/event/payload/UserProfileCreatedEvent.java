package com.kartnova.user.event.payload;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileCreatedEvent {

    private final Long profileId;
    private final Long authUserId;
    private final String email;
}