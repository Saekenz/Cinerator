package at.saekenz.cinerator.model.movie;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long id) {
        super("Could not find movie: " + id);
    }

    public MovieNotFoundException(String title) {
        super(String.format("Could not find movie with %s: %s", title.matches("^tt\\d+$") ? "imdb_id" : "title", title));
    }

    public MovieNotFoundException(String type, String input) {
        super(String.format("Could not find movie with %s: %s", type, input));
    }
}
