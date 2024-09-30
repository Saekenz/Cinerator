package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MovieMapper implements EntityMapper<Movie, MovieDTO> {

    public Movie toMovie(MovieDTO movieDTO) {
        Movie movie = new Movie();

        movie.setTitle(movieDTO.getTitle());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setRuntime(movieDTO.getRuntime());
        movie.setImdbId(movieDTO.getImdbId());
        movie.setPosterUrl(movieDTO.getPosterUrl());
        movie.setReviews(List.of());

        return movie;
    }

    public Movie toMovie(MovieCreationDTO movieCreationDTO) {
        Movie movie = new Movie();

        movie.setTitle(movieCreationDTO.title());
        movie.setReleaseDate(movieCreationDTO.releaseDate());
        movie.setRuntime(movieCreationDTO.runtime());
        movie.setImdbId(movieCreationDTO.imdbId());
        movie.setPosterUrl(movieCreationDTO.posterUrl());
        movie.setGenres(Set.of());
        movie.setCountries(Set.of());
        movie.setReviews(List.of());

        return movie;
    }

    public MovieDTO toDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();

        movieDTO.setId(movie.getId());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setReleaseDate(movie.getReleaseDate());
        movieDTO.setRuntime(movie.getRuntime());

        // Set directors
        String directors = Optional.ofNullable(movie.getCastInfos())
                .orElse(Collections.emptySet())
                .stream()
                .filter(c -> "Director".equals(c.getRole().getRole()))
                .map(CastInfo::getPersonName)
                .collect(Collectors.joining(", "));
        movieDTO.setDirector(directors);

        // Set genres
        String genres = convertCollectionToString(movie.getGenres(), Genre::getName);
        movieDTO.setGenre(genres);

        // Set countries
        String countries = convertCollectionToString(movie.getCountries(), Country::getName);
        movieDTO.setCountry(countries);

        movieDTO.setImdbId(movie.getImdbId());
        movieDTO.setPosterUrl(movie.getPosterUrl());

        return movieDTO;
    }

    private <T> String convertCollectionToString(Collection<T> collection, Function<T, String> mapper) {
        return Optional.ofNullable(collection)
                .orElse(Collections.emptyList())
                .stream()
                .map(mapper)
                .collect(Collectors.joining(", "));
    }
}
