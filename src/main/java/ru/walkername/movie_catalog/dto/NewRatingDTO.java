package ru.walkername.movie_catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class NewRatingDTO {

    @Min(value = 0, message = "Rating should be greater than 0")
    @Max(value = 10, message = "Rating should be less than 10")
    private double rating;

    @Min(value = 0, message = "Rating should be greater than 0")
    @Max(value = 10, message = "Rating should be less than 10")
    private double oldRating;

    private boolean update;

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getOldRating() {
        return oldRating;
    }

    public void setOldRating(int oldRating) {
        this.oldRating = oldRating;
    }
}
