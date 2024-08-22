package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.user.EUserSearchParams;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException() {
        super("Could not find any movies.");
    }

    public MovieNotFoundException(Long id) {
        super("Could not find movie: " + id);
    }

    public MovieNotFoundException(String title) {
        super(String.format("Could not find movie with %s: %s", title.matches("^tt\\d+$") ? "imdb_id" : "title", title));
    }

    public MovieNotFoundException(EMovieSearchParams searchParam, String input) {
        super(String.format("Could not find any movies with %s: %s", searchParam.getParamName(), input));
    }
}
