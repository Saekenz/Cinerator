package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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



    @Query("SELECT m FROM Movie m LEFT JOIN m.genres g LEFT JOIN m.countries c WHERE " +
            "(LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')) OR :title IS NULL) AND " +
            "(m.releaseDate = :releaseDate OR CAST(:releaseDate as timestamp) IS NULL) AND " +
            "(CAST(EXTRACT(YEAR FROM m.releaseDate) AS INTEGER) = :releaseYear OR :releaseYear IS NULL) AND " +
            "(LOWER(m.runtime) LIKE LOWER(CONCAT('%', :runtime, '%')) OR :runtime IS NULL) AND " +
            "(LOWER(m.imdbId) LIKE LOWER(CONCAT('%', :imdbId, '%')) OR :imdbId IS NULL) AND " +
            "(LOWER(g.name) LIKE LOWER(CONCAT('%', :genre, '%')) OR :genre IS NULL) AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :country, '%')) OR :country IS NULL)")
    List<Movie> findMoviesBySearchParams(@Param("title") String title,
                                         @Param("releaseDate") LocalDate releaseDate,
                                         @Param("releaseYear") Integer releaseYear,
                                         @Param("runtime") String runtime,
                                         @Param("imdbId") String imdbId,
                                         @Param("genre") String genre,
                                         @Param("country") String country);
}
