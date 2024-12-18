package at.saekenz.cinerator.model.review;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(Long id) {
        super("Could not find review: " + id);
    }

    public ReviewNotFoundException() { super("Could not find any reviews."); }
}
