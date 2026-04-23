package com.kartnova.user.constant;

public final class ApiPaths {

    private ApiPaths() {
    }

    public static final String API_BASE = "/api/users";

    public static final String PROFILE = "/profile";
    public static final String PROFILE_BY_ID = "/profile/{profileId}";
    public static final String PROFILE_BY_AUTH_USER_ID = "/profile/auth/{authUserId}";
    public static final String PROFILE_DEACTIVATE = "/profile/{profileId}/deactivate";

    public static final String ADDRESSES = "/profile/{profileId}/addresses";
    public static final String ADDRESS_BY_ID = "/profile/{profileId}/addresses/{addressId}";
    public static final String DEFAULT_ADDRESS = "/profile/{profileId}/addresses/default";
    public static final String SET_DEFAULT_ADDRESS = "/profile/{profileId}/addresses/{addressId}/default";

    public static final String INTERNAL = "/internal";
    public static final String INTERNAL_PROFILE_BY_AUTH_USER_ID = INTERNAL + "/profile/auth/{authUserId}";
    public static final String INTERNAL_DEFAULT_ADDRESS = INTERNAL + "/profile/{profileId}/default-address";
}