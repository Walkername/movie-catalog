package ru.walkername.movie_catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.walkername.movie_catalog.models.Movie;

import java.util.List;

@Repository
public interface MoviesRepository extends JpaRepository<Movie, Integer> {

    List<Movie> findByTitleStartingWith(String title);
}
