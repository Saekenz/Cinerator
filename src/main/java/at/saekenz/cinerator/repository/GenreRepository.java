package at.saekenz.cinerator.repository;


import at.saekenz.cinerator.model.genre.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
