package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.genre.Genre;

import java.util.List;
import java.util.Optional;

public interface IGenreService {

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    Genre getReferenceById(Long id);

    Genre save(Genre genre);

    void deleteById(Long id);
}
