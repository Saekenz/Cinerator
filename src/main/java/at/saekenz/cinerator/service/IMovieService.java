package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;

import java.util.List;
import java.util.Optional;

public interface IMovieService {

    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie save(Movie movie);

    void deleteById(Long id);

    List<Movie> findByGenre(String genre);

    List<Movie> findByDirector(String director);
}
