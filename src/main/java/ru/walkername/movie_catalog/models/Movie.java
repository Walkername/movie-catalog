package ru.walkername.movie_catalog.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Movie title should not be empty")
    @Size(min = 1, max = 50, message = "Movie title should be greater than 1 and less than 50 characters")
    @Column(name = "title")
    private String title;

    @Min(value = 0, message = "Release year cannot be negative")
    @Column(name = "release_year")
    private int releaseYear;

    @Size(max = 500, message = "Description should be less than 500 characters")
    @Column(name = "description")
    private String description;

    @Column(name = "average_rating")
    private double averageRating;

    @Column(name = "scores")
    private int scores;

    public Movie() {

    }

    public Movie(String title, int releaseYear, String description, double averageRating, int scores) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
        this.averageRating = averageRating;
        this.scores = scores;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", description='" + description + '\'' +
                ", averageRating=" + averageRating +
                ", scores=" + scores +
                '}';
    }
}
