package ru.walkername.movie_catalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.walkername.movie_catalog.dto.MovieDetails;
import ru.walkername.movie_catalog.dto.RatingsResponse;
import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.models.Rating;
import ru.walkername.movie_catalog.repositories.MoviesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;

    private final String RATING_SERVICE_API;

    @Autowired
    public MoviesService(
            MoviesRepository moviesRepository,
            @Value("${rating.service.url}") String RATING_SERVICE_API
    ) {
        this.moviesRepository = moviesRepository;
        this.RATING_SERVICE_API = RATING_SERVICE_API;
    }

    @Transactional
    public void save(Movie movie) {
        moviesRepository.save(movie);
    }

    public Movie findOne(int id) {
        Optional<Movie> movie = moviesRepository.findById(id);

        RestTemplate restTemplate = new RestTemplate();
        String url = RATING_SERVICE_API + "/ratings/movie/" + id;

        // TODO: use endpoint to get ready average rating
        // i think computation of average rating with the help of sql
        // will be better
        RatingsResponse ratingsResponse = restTemplate.getForObject(url, RatingsResponse.class);
        List<Rating> ratings = Objects.requireNonNull(ratingsResponse).getRatings();
        double averageRating = 0.0;
        if (ratings != null) {
            for (Rating rating : ratings) {
                averageRating += rating.getRating();
            }
            if (!ratings.isEmpty()) {
                averageRating /= ratings.size();
            }
        }

        if (movie.isPresent()) {
            movie.get().setAverageRating(averageRating);
        }

        return movie.orElse(null);
    }

    public List<Movie> getAllMovies() {
        return moviesRepository.findAll();
    }

    public List<MovieDetails> getMoviesByUser(int id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = RATING_SERVICE_API + "/ratings/user/" + id;

        RatingsResponse ratingsResponse = restTemplate.getForObject(url, RatingsResponse.class);
        List<Rating> ratings = Objects.requireNonNull(ratingsResponse).getRatings();

        List<Integer> movieIds = new ArrayList<>();
        if (ratings != null) {
            for (Rating rating : ratings) {
                movieIds.add(rating.getMovieId());
            }
        }

        List<MovieDetails> movies = new ArrayList<>();
        List<Movie> mov = moviesRepository.findAllById(movieIds);
        // TODO: improve algorithm, because this will be very slow with big amount of data
        if (ratings != null) {
            for (Rating rating : ratings) {
                for (Movie movie : mov) {
                    if (rating.getMovieId() == movie.getId()) {
                        MovieDetails movieDetails = new MovieDetails(movie, rating.getUserId(), rating.getRating());
                        movies.add(movieDetails);
                    }
                }
            }
        }

        return movies;
    }

    @Transactional
    public void update(int id, Movie updatedMovie) {
        updatedMovie.setId(id);
        Optional<Movie> movie = moviesRepository.findById(id);
        movie.ifPresent(value -> updatedMovie.setAverageRating(value.getAverageRating()));
    }

    public void delete(int id) {
        moviesRepository.deleteById(id);
    }

}
