package com.moviesAPI.moviesAPI.exceptions;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException(String errorMessage) {
        super(errorMessage);
    }
}
