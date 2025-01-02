package ru.walkername.movie_catalog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class MovieDTO {

    @NotEmpty(message = "Movie title should not be empty")
    @Size(min = 1, max = 50, message = "Movie title should be greater than 1 and less than 50 characters")
    private String title;

    @Min(value = 0, message = "Release year cannot be negative")
    private int releaseYear;

    @Size(max = 500, message = "Description should be less than 500 characters")
    private String description;

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
}
