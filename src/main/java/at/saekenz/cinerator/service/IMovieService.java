package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IMovieService {

    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie getReferenceById(Long id);

    List<Movie> findByTitle(String title);

    Movie save(Movie movie);

    void deleteById(Long id);

    List<Movie> findByGenre(String genre);

    List<Movie> findByCountry(String country);

    List<Movie> findByYear(int year);

    Optional<Movie> findByImdbId(String imdbId);

    Page<Movie> findAll(int page, int size, String sortBy, String sortDirection);
}
