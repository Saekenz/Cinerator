package at.saekenz.cinerator.model.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Could not find any users.");
    }

    public UserNotFoundException(Long id) {
        super("Could not find user: " + id);
    }

    public UserNotFoundException(EUserSearchParam searchParam, String input) {
        super(String.format("Could not find any users with %s: %s", searchParam.getParamName(), input));
    }
}
