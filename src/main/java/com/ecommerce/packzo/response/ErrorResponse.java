package com.ecommerce.packzo.response;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}