package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public Person getReferenceById(Long id) {
        return personRepository.getReferenceById(id);
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public void deleteById(Long id) {
        personRepository.deleteById(id);
    }

    @Override
    public List<Person> findPersonsBySearchParams(String name, LocalDate birthDate, LocalDate deathDate,
                                                  String height, String country, Integer age) {
        return personRepository.findPersonsBySearchParams(name, birthDate, deathDate, height, country, age);
    }

    @Override
    public List<Movie> findMoviesByPersonIdAndRole(Long personId, String role) {
        personRepository.findById(personId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Person with" +
                        " id %s could not be found!", personId)));
        return personRepository.findMoviesByPersonIdAndRole(personId, role);
    }

    @Override
    public Page<Person> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return personRepository.findAll(pageable);
    }

    @Override
    public Country findCountryByPersonId(Long personId) {
        return personRepository.findById(personId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Person with" +
                        " id %s could not be found!", personId))).getBirthCountry();
    }
}
