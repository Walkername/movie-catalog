package ru.walkername.movie_catalog.dto;

import ru.walkername.movie_catalog.models.Movie;

public class MovieDetails extends Movie {

    public MovieDetails() {

    }

    public MovieDetails(Movie movie, int userId, double rating) {
        super(movie.getTitle(), movie.getReleaseYear(), movie.getDescription(), movie.getAverageRating(), movie.getScores());
        this.userId = userId;
        this.rating = rating;
    }

    private int userId;

    private double rating;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
