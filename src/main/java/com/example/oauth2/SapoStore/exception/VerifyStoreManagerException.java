package com.example.oauth2.SapoStore.exception;

import java.io.Serial;

public class VerifyStoreManagerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public VerifyStoreManagerException(String message) {
        super(message);
    }
}
