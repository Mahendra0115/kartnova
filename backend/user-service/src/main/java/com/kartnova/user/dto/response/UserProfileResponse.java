package com.kartnova.user.dto.response;

import com.kartnova.user.enums.AccountStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse {

    private Long id;
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}