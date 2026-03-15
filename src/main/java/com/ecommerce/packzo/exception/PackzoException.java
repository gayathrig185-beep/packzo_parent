package com.ecommerce.packzo.exception;

import lombok.Data;

@Data
public class PackzoException extends RuntimeException{
    private String errorCode;
    private String text ;
    private String errorMessage;

    public PackzoException(String errorCode, String text , String errorMessage){
        this.errorCode = errorCode;
        this.text = text;
        this.errorMessage = errorMessage;
    }

}
