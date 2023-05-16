package com.example.exceptions;

public class PasswordExpiredException extends Exception{
    public PasswordExpiredException(String message) {
        super(message);
    }

}
