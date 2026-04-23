package com.kartnova.user.constant;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    public static final String INTERNAL_SERVER_ERROR = "USR_500_001";
    public static final String INVALID_REQUEST = "USR_400_001";
    public static final String VALIDATION_FAILED = "USR_400_002";

    public static final String USER_PROFILE_NOT_FOUND = "USR_404_001";
    public static final String USER_PROFILE_ALREADY_EXISTS = "USR_409_001";

    public static final String USER_ADDRESS_NOT_FOUND = "USR_404_002";
    public static final String DEFAULT_ADDRESS_NOT_FOUND = "USR_404_003";

    public static final String BUSINESS_VALIDATION_FAILED = "USR_400_003";
}