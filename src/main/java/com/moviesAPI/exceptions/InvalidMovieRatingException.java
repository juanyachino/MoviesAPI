package com.moviesAPI.exceptions;

public class InvalidMovieRatingException extends Exception {
    public InvalidMovieRatingException(String errorMessage) {
        super(errorMessage);
    }
}
