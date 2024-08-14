package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE LOWER(m.genre) = LOWER(:genre)")
    List<Movie> findByGenre(@Param("genre") String genre);

    List<Movie> findByDirector(String director);

    List<Movie> findByTitle(String title);

    List<Movie> findByCountry(String country);

    @Query("SELECT m FROM Movie m WHERE EXTRACT(YEAR FROM m.release_date) = :yearReleased")
    List<Movie> findByYearReleased(@Param("yearReleased") int yearReleased);

    @Query("SELECT m FROM Movie m WHERE LOWER(m.imdb_id) = LOWER(:imdb_id)")
    Optional<Movie> findByImdb_id(@Param("imdb_id") String imdb_id);
}
