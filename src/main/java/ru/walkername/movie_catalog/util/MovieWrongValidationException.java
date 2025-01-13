package ru.walkername.movie_catalog.util;

public class MovieWrongValidationException extends RuntimeException {

    public MovieWrongValidationException(String msg) {
        super(msg);
    }

}
