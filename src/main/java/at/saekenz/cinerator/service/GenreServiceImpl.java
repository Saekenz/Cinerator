package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTO;
import at.saekenz.cinerator.model.genre.GenreMapper;
import at.saekenz.cinerator.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements IGenreService {

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private GenreMapper genreMapper;


    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }

    @Override
    public Genre findGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Genre with id %s could not be found!", id)));
    }

    @Override
    public Genre getReferenceById(Long id) {
        return genreRepository.getReferenceById(id);
    }

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public void deleteById(Long id) {
        findGenreById(id);
        genreRepository.deleteById(id);
    }

    @Override
    public Page<Genre> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return genreRepository.findAll(pageable);
    }

    @Override
    public Genre createGenre(GenreDTO genreDTO) {
        Genre newGenre = genreMapper.toGenre(genreDTO);

        return save(newGenre);
    }

    @Override
    public Genre updateGenre(Long id, GenreDTO genreDTO) {
        Genre existingGenre = findGenreById(id);
        existingGenre.setName(genreDTO.name());

        return save(existingGenre);
    }
}
