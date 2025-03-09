package ru.walkername.movie_catalog.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.walkername.movie_catalog.dto.MovieDTO;
import ru.walkername.movie_catalog.dto.MovieDetails;
import ru.walkername.movie_catalog.dto.NewRatingDTO;
import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.services.MoviesService;
import ru.walkername.movie_catalog.util.MovieErrorResponse;
import ru.walkername.movie_catalog.util.MovieWrongValidationException;

import java.util.List;

@RestController
@RequestMapping("/movies")
@CrossOrigin
public class MoviesController {

    private final MoviesService moviesService;
    private final ModelMapper modelMapper;

    @Autowired
    public MoviesController(MoviesService moviesService, ModelMapper modelMapper) {
        this.moviesService = moviesService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(
            @RequestBody @Valid MovieDTO movieDTO,
            BindingResult bindingResult
    ) {
        validateMovie(bindingResult);
        Movie movie = convertToMovie(movieDTO);
        moviesService.save(movie);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public List<Movie> index(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "down", required = false, defaultValue = "true") boolean down
    ) {
        return moviesService.getAllMoviesWithPagination(page, limit, down);
    }

    @GetMapping("/{id}")
    public Movie getMovie(
            @PathVariable("id") int id
    ) {
        return moviesService.findOne(id);
    }

    @GetMapping("/user/{id}")
    public List<MovieDetails> getMoviesByUserId(
            @PathVariable("id") int id,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            @RequestParam(value = "down", required = false, defaultValue = "true") boolean byDate
    ) {
        return moviesService.getMoviesByUser(id, page, limit, byDate);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<HttpStatus> update(
            @PathVariable("id") int id,
            @RequestBody @Valid MovieDTO movieDTO,
            BindingResult bindingResult
    ) {
        validateMovie(bindingResult);
        Movie movie = moviesService.findOne(id);
        if (movie != null) {
            modelMapper.map(movieDTO, movie);
            moviesService.save(movie);
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/update-avg-rating/{id}")
    public ResponseEntity<HttpStatus> updateAvgRating(
            @PathVariable("id") int id,
            @RequestBody @Valid NewRatingDTO ratingDTO,
            BindingResult bindingResult
    ) {
        validateMovie(bindingResult);
        moviesService.updateAverageRating(id, ratingDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(
            @PathVariable("id") int id
    ) {
        moviesService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/count")
    public long getMoviesNumber() {
        return moviesService.getMoviesNumber();
    }

    @GetMapping("/search")
    public List<Movie> search(
        @RequestParam(value = "query") String query
    ) {
        return moviesService.findByTitleStartingWith(query);
    }

    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handleException(MovieWrongValidationException ex) {
        MovieErrorResponse response = new MovieErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private void validateMovie(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new MovieWrongValidationException(errorMsg.toString());
        }
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        if (movie == null) {
            return null;
        }
        return modelMapper.map(movie, MovieDTO.class);
    }

    private Movie convertToMovie(MovieDTO movieDTO) {
        return modelMapper.map(movieDTO, Movie.class);
    }

}
