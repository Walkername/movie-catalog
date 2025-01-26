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

    /**
     * Method to update field of average rating of specific movie
     * @param id indicates the movie that will be updated
     * @param ratingDTO new rating to update average rating
     *                  <br><b>Structure:</b>
     *                  <br>rating - indicates new rating
     *                  <br>oldRating - indicates old rating
     *                  <br>update - indicates whether this is a new rating or a replacement for the old one
     */
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

    /**
     * Method to get all movies from DB
     * @return list of movies
     */
    public List<Movie> getAllMovies() {
        return moviesRepository.findAll();
    }

    /**
     * Method to get all movies from DB with pagination
     * Method is need to do lists of movies on the site
     * @param page number of page
     * @param moviesPerPage number of movies that will be in the list
     *                      (all movies are split by this number, you give only part by page number)
     * @param down default 'true' -> descending rating order; 'false' -> ascending.
     * @return list of movies
     */
    public List<Movie> getAllMoviesWithPagination(int page, int moviesPerPage, boolean down) {
        Sort sort = down
                ? Sort.by("averageRating").descending()
                : Sort.by("averageRating").ascending();
        return moviesRepository.findAll(PageRequest.of(page, moviesPerPage, sort)).getContent();
    }

    /**
     * Method to get movies from DB, which was rated by specific user
     *
     * @param id indicates the user ID whose rated movies you want to get
     * @return list of movies with details of rating
     */
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

        // Building list with movie details: title, release year, rating from user, etc.
        List<MovieDetails> movieDetailsList = new ArrayList<>();
        // TODO: maybe improve algorithm, because this will be very slow with big amount of data
        for (Rating rating : ratings) {
            for (Movie movie : ratedMovies) {
                if (rating.getMovieId() == movie.getId()) {
                    MovieDetails movieDetails = new MovieDetails(movie, rating);
                    movieDetailsList.add(movieDetails);
                }
            }
        }

        return movieDetailsList;
    }

}
