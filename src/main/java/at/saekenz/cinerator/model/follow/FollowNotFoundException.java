package at.saekenz.cinerator.model.follow;

public class FollowNotFoundException extends RuntimeException {

    public FollowNotFoundException() {
        super("Could not find any following relationships!");
    }

    public FollowNotFoundException(FollowKey key) {
        super(String.format("User %s is not currently following user %s!",
                key.getFollowerId(), key.getUserId()));
    }
}
