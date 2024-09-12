package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.repository.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreServiceImpl implements IGenreService {

    @Autowired
    private GenreRepository genreRepository;


    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
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
        genreRepository.deleteById(id);
    }
}
