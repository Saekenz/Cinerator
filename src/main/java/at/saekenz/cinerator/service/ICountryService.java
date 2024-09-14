package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;

import java.util.List;
import java.util.Optional;

public interface ICountryService {

    List<Country> findAll();

    Optional<Country> findById(Long id);

    Country getReferenceById(Long id);

    Country save(Country country);

    void deleteById(Long id);
}
