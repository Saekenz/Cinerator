package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
