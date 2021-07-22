package com.moviesAPI.exceptions;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
