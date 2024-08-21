package at.saekenz.cinerator.model.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Could not find user: " + id);
    }

    public UserNotFoundException(String type, String input) {
        super(String.format("Could not find user with %s: %s", type, input));
    }
}
