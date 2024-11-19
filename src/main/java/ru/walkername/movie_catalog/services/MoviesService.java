package ru.walkername.movie_catalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.repositories.MoviesRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MoviesService {

    private final MoviesRepository moviesRepository;

    @Autowired
    public MoviesService(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
    }

    @Transactional
    public void save(Movie movie) {
        moviesRepository.save(movie);
    }

    public Movie findOne(int id) {
        Optional<Movie> movie = moviesRepository.findById(id);
        return movie.orElse(null);
    }

    public List<Movie> getAllMovies() {
        return moviesRepository.findAll();
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
