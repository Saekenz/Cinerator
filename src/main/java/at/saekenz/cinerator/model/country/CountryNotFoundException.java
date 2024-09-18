package at.saekenz.cinerator.model.country;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException() {
        super("Could not find any countries.");
    }

    public CountryNotFoundException(Long id) {
        super(String.format("Could not find any country with id: " + id));
    }

    public CountryNotFoundException(String country) {
        super(String.format("Could not find any country with name: " + country));
    }
}
