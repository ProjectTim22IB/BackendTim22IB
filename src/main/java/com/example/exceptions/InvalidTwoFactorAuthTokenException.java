package com.example.exceptions;

public class InvalidTwoFactorAuthTokenException extends Exception{
    public InvalidTwoFactorAuthTokenException(String message) {
        super(message);
    }
}
