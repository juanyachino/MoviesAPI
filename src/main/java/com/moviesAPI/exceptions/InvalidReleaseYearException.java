package com.moviesAPI.exceptions;

public class InvalidReleaseYearException extends Exception {
    public InvalidReleaseYearException(String errorMessage) {
        super(errorMessage);
    }
}
