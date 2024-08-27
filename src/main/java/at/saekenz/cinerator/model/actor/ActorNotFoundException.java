package at.saekenz.cinerator.model.actor;

public class ActorNotFoundException extends RuntimeException {

    public ActorNotFoundException() {
        super("Could not find any actors.");
    }

    public ActorNotFoundException(Long id) {
        super("Could not find any actor with id: " + id);
    }

    public ActorNotFoundException(EActorSearchParam searchParam, String input) {
        super(String.format("Could not find any actor with %s: %s", searchParam.getParamName(), input));
    }
}
