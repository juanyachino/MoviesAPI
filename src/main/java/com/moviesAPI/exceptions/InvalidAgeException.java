package com.moviesAPI.exceptions;

public class InvalidAgeException extends Exception {
    public InvalidAgeException(String errorMessage) {
        super(errorMessage);
    }
}
