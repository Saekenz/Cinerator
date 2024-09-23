package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ICountryService {

    List<Country> findAll();

    Optional<Country> findById(Long id);

    Country findCountryById(Long id);

    Optional<Country> findByName(String name);

    Country getReferenceById(Long id);

    Country save(Country country);

    void deleteById(Long id);

    Page<Country> findAllPaged(int page, int size, String sortField, String sortDirection);

    Country updateCountry(Long id, CountryDTO countryDTO);

    Country createCountry(CountryDTO countryDTO);

}
