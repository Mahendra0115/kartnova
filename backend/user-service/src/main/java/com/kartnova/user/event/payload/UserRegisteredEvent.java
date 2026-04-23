package com.kartnova.user.event.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisteredEvent {

    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}