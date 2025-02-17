package ru.walkername.movie_catalog.dto;

import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.models.Rating;

import java.util.Date;

public class MovieDetails {

    private int userId;

    private int movieId;

    private String title;

    private int releaseYear;

    private double rating;

    private double averageRating;

    private int scores;

    private Date date;

    public MovieDetails() {

    }

    public MovieDetails(Movie movie, Rating rating) {
        this.userId = rating.getUserId();
        this.movieId = movie.getId();
        this.rating = rating.getRating();
        this.title = movie.getTitle();
        this.releaseYear = movie.getReleaseYear();
        this.averageRating = movie.getAverageRating();
        this.scores = movie.getScores();
        this.date = rating.getDate();
    }

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

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getScores() {
        return scores;
    }

    public void setScores(int scores) {
        this.scores = scores;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
