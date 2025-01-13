package ru.walkername.movie_catalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.walkername.movie_catalog.dto.MovieDetails;
import ru.walkername.movie_catalog.dto.NewRatingDTO;
import ru.walkername.movie_catalog.dto.RatingsResponse;
import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.models.Rating;
import ru.walkername.movie_catalog.repositories.MoviesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;

    private final String RATING_SERVICE_API;

    private final RestTemplate restTemplate;

    @Autowired
    public MoviesService(
            MoviesRepository moviesRepository,
            @Value("${rating.service.url}") String RATING_SERVICE_API,
            RestTemplate restTemplate) {
        this.moviesRepository = moviesRepository;
        this.RATING_SERVICE_API = RATING_SERVICE_API;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void save(Movie movie) {
        moviesRepository.save(movie);
    }

    public Movie findOne(int id) {
        Optional<Movie> movie = moviesRepository.findById(id);
        return movie.orElse(null);
    }

    @Transactional
    public void update(int id, Movie updatedMovie) {
        updatedMovie.setId(id);
        moviesRepository.save(updatedMovie);
    }

    @Transactional
    public void delete(int id) {
        moviesRepository.deleteById(id);
    }

    @Transactional
    public void updateAverageRating(int id, NewRatingDTO ratingDTO) {
        Optional<Movie> movie = moviesRepository.findById(id);
        movie.ifPresent(value -> {
            double newRating = ratingDTO.getRating();
            double oldRating = ratingDTO.getOldRating();
            boolean isUpdate = ratingDTO.isUpdate();

            int scores = value.getScores();
            double averageRating = value.getAverageRating();
            double newAverageRating;

            if (!isUpdate) {
                newAverageRating = (averageRating * scores + newRating) / (scores + 1);
                value.setScores(scores + 1);
            } else {
                newAverageRating = (averageRating * scores - oldRating + newRating) / scores;
            }

            value.setAverageRating(newAverageRating);
        });
    }

    public long getMoviesNumber() {
        return moviesRepository.count();
    }

    public List<Movie> getAllMovies() {
        return moviesRepository.findAll();
    }

    public List<Movie> getAllMoviesWithPagination(int page, int moviesPerPage, boolean down) {
        Sort sort = down
                ? Sort.by("averageRating").descending()
                : Sort.by("averageRating").ascending();
        return moviesRepository.findAll(PageRequest.of(page, moviesPerPage, sort)).getContent();
    }

    public List<MovieDetails> getMoviesByUser(int id) {
        String url = RATING_SERVICE_API + "/ratings/user/" + id;

        // Getting Rating list by user_id
        RatingsResponse ratingsResponse = restTemplate.getForObject(url, RatingsResponse.class);
        if (ratingsResponse == null) {
            return new ArrayList<>();
        }
        List<Rating> ratings = ratingsResponse.getRatings();

        // Building list with movieIds from Rating list
        List<Integer> movieIds = new ArrayList<>();
        for (Rating rating : ratings) {
            movieIds.add(rating.getMovieId());
        }

        // Getting Movie list by movieIds list
        List<Movie> ratedMovies = moviesRepository.findAllById(movieIds);

        List<MovieDetails> movieDetailsList = new ArrayList<>();
        // TODO: improve algorithm, because this will be very slow with big amount of data
        for (Rating rating : ratings) {
            for (Movie movie : ratedMovies) {
                if (rating.getMovieId() == movie.getId()) {
                    MovieDetails movieDetails = new MovieDetails(movie, rating.getUserId(), rating.getRating());
                    movieDetailsList.add(movieDetails);
                }
            }
        }

        return movieDetailsList;
    }

}
