package ru.walkername.movie_catalog.dto;

import ru.walkername.movie_catalog.models.Rating;

import java.util.List;

public class RatingsResponse {

    private List<Rating> ratings;

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
}
