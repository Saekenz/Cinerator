package at.saekenz.cinerator.model.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Could not find user: " + id);
    }
}
