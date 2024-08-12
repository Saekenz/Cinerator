package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByGenre(String genre);

    List<Movie> findByDirector(String director);
}
