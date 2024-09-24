package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IGenreService {

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    Genre findGenreById(Long id);

    Genre getReferenceById(Long id);

    Genre save(Genre genre);

    void deleteById(Long id);

    Page<Genre> findAllPaged(int page, int size, String sortField, String sortDirection);

    Genre createGenre(GenreDTO roleDTO);

    Genre updateGenre(Long id, GenreDTO roleDTO);


}
