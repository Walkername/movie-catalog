package ru.walkername.movie_catalog.util;

public class MovieNotCreatedException extends RuntimeException {

    public MovieNotCreatedException(String msg) {
        super(msg);
    }

}
