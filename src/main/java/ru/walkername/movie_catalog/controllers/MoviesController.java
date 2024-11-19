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
import ru.walkername.movie_catalog.models.Movie;
import ru.walkername.movie_catalog.services.MoviesService;
import ru.walkername.movie_catalog.util.MovieErrorResponse;
import ru.walkername.movie_catalog.util.MovieNotCreatedException;

import java.util.List;

@RestController
@RequestMapping("/movies")
@CrossOrigin
public class MoviesController {

    private final MoviesService moviesService;

    @Autowired
    public MoviesController(MoviesService moviesService) {
        this.moviesService = moviesService;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(
            @RequestBody @Valid MovieDTO movieDTO,
            BindingResult bindingResult
    ) {
        Movie movie = convertToMovie(movieDTO);

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();

            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }

            throw new MovieNotCreatedException(errorMsg.toString());
        }

        moviesService.save(movie);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping()
    public List<Movie> index(

    ) {
        return moviesService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovie(
            @PathVariable("id") int id
    ) {
        return moviesService.findOne(id);
    }

    @ExceptionHandler
    private ResponseEntity<MovieErrorResponse> handleException(MovieNotCreatedException ex) {
        MovieErrorResponse response = new MovieErrorResponse(
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        ModelMapper modelMapper = new ModelMapper();
        if (movie == null) {
            return null;
        }
        return modelMapper.map(movie, MovieDTO.class);
    }

    private Movie convertToMovie(MovieDTO movieDTO) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(movieDTO, Movie.class);
    }

}
