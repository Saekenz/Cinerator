package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryMapper;
import at.saekenz.cinerator.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements ICountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryMapper countryMapper;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<Country> findById(Long id) {
        return countryRepository.findById(id);
    }

    @Override
    public Country findCountryById(Long id) {
        return countryRepository.findById(id).orElseThrow(
                () ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Country with id %s could not be found!", id)));
    }

    @Override
    public Optional<Country> findByName(String name) {
        return countryRepository.findByName(name);
    }

    @Override
    public Country getReferenceById(Long id) {
        return countryRepository.getReferenceById(id);
    }

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public void deleteById(Long id) {
        findCountryById(id);
        countryRepository.deleteById(id);
    }

    @Override
    public Page<Country> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return countryRepository.findAll(pageable);
    }

    @Override
    public Country updateCountry(Long id, CountryDTO countryDTO) {
        Country existingCountry = findCountryById(id);
        existingCountry.setName(countryDTO.name());

        return save(existingCountry);
    }

    @Override
    public Country createCountry(CountryDTO countryDTO) {
        Country newCountry = countryMapper.toCountry(countryDTO);

        return save(newCountry);
    }
}
