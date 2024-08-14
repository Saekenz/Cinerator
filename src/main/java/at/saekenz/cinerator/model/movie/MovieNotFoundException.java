package at.saekenz.cinerator.model.movie;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long id) {
        super("Could not find movie: " + id);
    }

    public MovieNotFoundException(String imdb_id) { super("Could not find movie with imdb_id: " + imdb_id); }
}
