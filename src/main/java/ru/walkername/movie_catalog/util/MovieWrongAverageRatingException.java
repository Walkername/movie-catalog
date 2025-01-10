package ru.walkername.movie_catalog.util;

public class MovieWrongAverageRatingException extends RuntimeException {

    public MovieWrongAverageRatingException(String message) {
        super(message);
    }

}
