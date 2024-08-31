package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements IMovieService{

    @Autowired
    public MovieRepository movieRepository;

    @Override
    public List<Movie> findAll() {
       return movieRepository.findAll();
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public List<Movie> findByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    @Override
    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteById(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> findByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    @Override
    public List<Movie> findByDirector(String director) {
        return movieRepository.findByDirector(director);
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return movieRepository.findByCountry(country);
    }

    @Override
    public List<Movie> findByYear(int year) {
        return movieRepository.findByYearReleased(year);
    }

    @Override
    public Optional<Movie> findByImdb_id(String imdb_id) {
        return movieRepository.findByImdb_id(imdb_id);
    }

    @Override
    public Page<Movie> findAll(int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return movieRepository.findAll(pageable);
    }


}
