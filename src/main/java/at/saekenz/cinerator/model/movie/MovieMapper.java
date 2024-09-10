package at.saekenz.cinerator.model.movie;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieMapper {

    public MovieDTO toDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();

        movieDTO.setId(movie.getId());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setReleaseDate(movie.getReleaseDate());
        movieDTO.setRuntime(movie.getRuntime());
        movieDTO.setDirector(movie.getDirector());
        movieDTO.setGenre(movie.getGenre());
        movieDTO.setCountry(movie.getCountry());
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
        movie.setGenre(movieDTO.getGenre());
        movie.setCountry(movieDTO.getCountry());
        movie.setImdbId(movieDTO.getImdbId());
        movie.setPosterUrl(movieDTO.getPosterUrl());
        movie.setReviews(List.of());
        movie.setActors(List.of());

        return movie;
    }
}
