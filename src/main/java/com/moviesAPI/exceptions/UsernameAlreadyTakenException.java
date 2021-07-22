package com.moviesAPI.exceptions;

public class UsernameAlreadyTakenException extends Exception {
    public UsernameAlreadyTakenException(String errorMessage) {
        super(errorMessage);
    }
}
