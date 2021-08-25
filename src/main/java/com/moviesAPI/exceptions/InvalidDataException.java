package com.moviesAPI.exceptions;

public class InvalidDataException extends Exception {
    public InvalidDataException(String errorMessage) {
        super(errorMessage);
    }
}
