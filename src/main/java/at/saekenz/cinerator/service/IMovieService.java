package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieCreationDTO;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewCreationDTO;
import at.saekenz.cinerator.model.review.ReviewUpdateDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IMovieService {

    List<Movie> findAll();

    Optional<Movie> findById(Long id);

    Movie findMovieById(Long id);

    Movie getReferenceById(Long id);

    List<Movie> findByTitle(String title);

    Movie save(Movie movie);

    void deleteById(Long id);

    List<Movie> findByGenre(String genre);

    List<Movie> findByCountry(String country);

    List<Movie> findByYear(int year);

    Optional<Movie> findByImdbId(String imdbId);

    Page<Movie> findAllPaged(int page, int size, String sortField, String sortDirection);

    Movie createMovie(MovieCreationDTO movieCreationDTO);

    Movie updateMovie(Long id, MovieCreationDTO movieCreationDTO);

    List<Movie> searchMovies(String title, LocalDate releaseDate, String runtime,
                             String imdbId, String genre, String country);

    List<Review> findReviewsByMovieId(Long id);

    Review findReviewByMovieId(Long movieId, Long reviewId);

    Review addReviewToMovie(Long movieId, ReviewCreationDTO reviewCreationDTO);

    Review editReviewForMovie(Long movieId, Long reviewId, ReviewUpdateDTO reviewUpdateDTO);

    void removeReviewFromMovie(Long movieId, Long reviewId);

    List<Person> findActorsByMovieId(Long movieId);

    List<Person> findDirectorsByMovieId(Long movieId);

    List<Genre> findGenresByMovieId(Long movieId);

    List<Country> findCountriesByMovieId(Long movieId);
}
