package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenres_Name(@Param("genre") String genre);

    List<Movie> findByCountries_Name(@Param("country") String country);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) = LOWER(:title)")
    List<Movie> findByTitle(@Param("title") String title);

    @Query("SELECT m FROM Movie m WHERE EXTRACT(YEAR FROM m.releaseDate) = :yearReleased")
    List<Movie> findByYearReleased(@Param("yearReleased") int yearReleased);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.imdbId) = LOWER(:imdbId)")
    Optional<Movie> findByImdbId(@Param("imdbId") String imdbId);

    @Query("SELECT r FROM Movie m JOIN m.reviews r WHERE " +
            "m.id = :movieId AND r.id = :reviewId")
    Optional<Review> findReviewByMovieId(@Param("movieId") Long movieId, @Param("reviewId") Long reviewId);

    @Query("SELECT c.person FROM Movie m JOIN m.castInfos c WHERE " +
            "c.role.id = 1 AND m.id = :movieId")
    List<Person> findActorsByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT c.person FROM Movie m JOIN m.castInfos c WHERE " +
            "c.role.id = 2 AND m.id = :movieId")
    List<Person> findDirectorsByMovieId(@Param("movieId") Long movieId);
}
