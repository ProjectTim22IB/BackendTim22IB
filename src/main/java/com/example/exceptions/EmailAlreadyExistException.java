package com.example.exceptions;

public class EmailAlreadyExistException extends Exception{

    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
