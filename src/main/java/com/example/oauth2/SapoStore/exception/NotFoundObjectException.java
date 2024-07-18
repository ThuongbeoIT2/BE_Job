package com.example.oauth2.SapoStore.exception;

public class NotFoundObjectException extends RuntimeException {
    private final String errorCode;
    private final String customMessage;

    public NotFoundObjectException(String errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return "Error Code: " + errorCode + ", Message: " + customMessage;
    }
}
