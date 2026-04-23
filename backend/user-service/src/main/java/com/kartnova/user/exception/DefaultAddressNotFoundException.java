package com.kartnova.user.exception;

public class DefaultAddressNotFoundException extends RuntimeException {

    public DefaultAddressNotFoundException(String message) {
        super(message);
    }
}