package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MovieMapper implements EntityMapper<Movie, MovieDTO> {

    public MovieDTO toDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();
        String genres = "";
        String countries = "";

        movieDTO.setId(movie.getId());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setReleaseDate(movie.getReleaseDate());
        movieDTO.setRuntime(movie.getRuntime());
        movieDTO.setDirector(movie.getDirector());

        if (movie.getGenres() != null) {
            genres = movie.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.joining(", "));
        }
        movieDTO.setGenre(genres);

        if (movie.getCountries() != null) {
            countries = movie.getCountries().stream()
                    .map(Country::getName)
                    .collect(Collectors.joining(", "));
        }
        movieDTO.setCountry(countries);

        movieDTO.setImdbId(movie.getImdbId());
        movieDTO.setPosterUrl(movie.getPosterUrl());

        return movieDTO;
    }

    public Movie toMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();

        movie.setTitle(movieDTO.getTitle());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setRuntime(movieDTO.getRuntime());
        movie.setDirector(movieDTO.getDirector());
        movie.setImdbId(movieDTO.getImdbId());
        movie.setPosterUrl(movieDTO.getPosterUrl());
        movie.setReviews(List.of());
        movie.setActors(List.of());

        return movie;
    }
}
