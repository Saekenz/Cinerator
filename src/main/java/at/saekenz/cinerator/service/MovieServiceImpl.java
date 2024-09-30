package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieCreationDTO;
import at.saekenz.cinerator.model.movie.MovieMapper;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewCreationDTO;
import at.saekenz.cinerator.model.review.ReviewUpdateDTO;
import at.saekenz.cinerator.repository.CountryRepository;
import at.saekenz.cinerator.repository.GenreRepository;
import at.saekenz.cinerator.repository.MovieRepository;
import at.saekenz.cinerator.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieServiceImpl implements IMovieService{

    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieMapper movieMapper;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<Movie> findAll() {
       return movieRepository.findAll();
    }

    @Override
    public Optional<Movie> findById(Long id) {
        return movieRepository.findById(id);
    }

    @Override
    public Movie findMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Movie with id %s could not be found!", id)));
    }

    @Override
    public Movie getReferenceById(Long id) {
        return movieRepository.getReferenceById(id);
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
        findMovieById(id);
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> findByGenre(String genre) {
        return movieRepository.findByGenres_Name(genre);
    }

    @Override
    public List<Movie> findByCountry(String country) {
        return movieRepository.findByCountries_Name(country);
    }

    @Override
    public List<Movie> findByYear(int year) {
        return movieRepository.findByYearReleased(year);
    }

    @Override
    public Optional<Movie> findByImdbId(String imdbId) {
        return movieRepository.findByImdbId(imdbId);
    }

    @Override
    public Page<Movie> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return movieRepository.findAll(pageable);
    }

    @Override
    public Movie createMovie(MovieCreationDTO movieCreationDTO) {
        Movie newMovie = movieMapper.toMovie(movieCreationDTO);

        Set<Genre> genres = Set.copyOf(genreRepository.findAllById(movieCreationDTO.genreIds()));
        newMovie.setGenres(genres);

        Set<Country> countries = Set.copyOf(countryRepository.findAllById(movieCreationDTO.countryIds()));
        newMovie.setCountries(countries);

        return save(newMovie);
    }

    @Override
    public Movie updateMovie(Long id, MovieCreationDTO movieCreationDTO) {
        Movie foundMovie = findMovieById(id);

        foundMovie.setTitle(movieCreationDTO.title());
        foundMovie.setReleaseDate(movieCreationDTO.releaseDate());
        foundMovie.setRuntime(movieCreationDTO.runtime());
        foundMovie.setImdbId(movieCreationDTO.imdbId());
        foundMovie.setPosterUrl(movieCreationDTO.posterUrl());

        Set<Genre> genreSet = new HashSet<>(genreRepository.findAllById(movieCreationDTO.genreIds()));
        foundMovie.setGenres(genreSet);

        Set<Country> countrySet = new HashSet<>(countryRepository.findAllById(movieCreationDTO.countryIds()));
        foundMovie.setCountries(countrySet);

        return save(foundMovie);
    }

    @Override
    public List<Movie> searchMovies(String title, LocalDate releaseDate, String runtime, String imdbId, String genre, String country) {
        return List.of();
    }

    @Override
    public List<Review> findReviewsByMovieId(Long id) {
        return findMovieById(id).getReviews();
    }

    @Override
    public Review findReviewByMovieId(Long movieId, Long reviewId) {
        return movieRepository.findReviewByMovieId(movieId, reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Review with id %s could not be found for Movie with id %s!",
                                reviewId, movieId)));
    }

    @Override
    public Review addReviewToMovie(Long movieId, ReviewCreationDTO reviewCreationDTO) {
        return null;
    }

    @Override
    public Review editReviewForMovie(Long movieId, Long reviewId, ReviewUpdateDTO reviewUpdateDTO) {
        return null;
    }

    @Override
    public void removeReviewFromMovie(Long movieId, Long reviewId) {
        if (findReviewByMovieId(movieId, reviewId) != null) {
            log.info("Review with id {} removed from Movie with id {}.", reviewId, movieId);
            reviewRepository.deleteById(reviewId);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Review with id %s was not found for Movie with id %s!",
                            reviewId, movieId));
        }
    }

    @Override
    public List<Person> findActorsByMovieId(Long movieId) {
        findMovieById(movieId);
        return movieRepository.findActorsByMovieId(movieId);
    }

    @Override
    public List<Person> findDirectorsByMovieId(Long movieId) {
        findMovieById(movieId);
        return movieRepository.findDirectorsByMovieId(movieId);
    }

    @Override
    public List<Genre> findGenresByMovieId(Long movieId) {
        return List.copyOf(findMovieById(movieId).getGenres());
    }

    @Override
    public List<Country> findCountriesByMovieId(Long movieId) {
        return List.copyOf(findMovieById(movieId).getCountries());
    }
}
