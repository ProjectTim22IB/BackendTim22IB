package com.example.exceptions;

public class TwoFactorAuthExpiredException extends Exception{
    public TwoFactorAuthExpiredException(String message) {
        super(message);
    }

}
