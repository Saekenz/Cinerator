package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.model.role.Role;
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
import java.util.Objects;
import java.util.Optional;

@Service
public class PersonServiceImpl implements IPersonService {
    private final ICountryService countryService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

    public PersonServiceImpl(ICountryService countryService) {
        this.countryService = countryService;
    }

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
        findPersonById(id);
        personRepository.deleteById(id);
    }

    @Override
    public List<Person> findPersonsBySearchParams(String name, LocalDate birthDate, LocalDate deathDate,
                                                  String height, String country, Integer age) {
        return personRepository.findPersonsBySearchParams(name, birthDate, deathDate, height, country, age);
    }

    @Override
    public List<Movie> findMoviesByPersonIdAndRole(Long personId, String role) {
        findPersonById(personId);
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
        return findPersonById(personId).getBirthCountry();
    }

    @Override
    public Person updatePerson(Long id, PersonDTO personDTO) {
        Person existingPerson = findPersonById(id);

        if (!Objects.equals(personDTO.getBirthCountry().id(), existingPerson.getBirthCountry().getId())) {
            Country updatedCountry = countryService.getReferenceById(personDTO.getBirthCountry().id());
            existingPerson.setBirthCountry(updatedCountry);
        }

        existingPerson.setName(personDTO.getName());
        existingPerson.setBirthDate(personDTO.getBirthDate());
        existingPerson.setDeathDate(personDTO.getDeathDate());
        existingPerson.setHeight(personDTO.getHeight());

        return save(existingPerson);
    }

    @Override
    public Person createPerson(PersonDTO personDTO) {
        Person newPerson = personMapper.toPerson(personDTO);
        Country birthCountry = countryService.getReferenceById(personDTO.getBirthCountry().id());

        newPerson.setBirthCountry(birthCountry);

        return save(newPerson);
    }

    @Override
    public Person findPersonById(Long id) {
        return personRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Person with id %s could not be found!", id)));
    }

    @Override
    public List<CastInfo> findCastInfosByPersonId(Long personId) {
        findPersonById(personId);
        return personRepository.findCastInfosByPersonId(personId);
    }

    @Override
    public List<Role> findRolesByPersonId(Long personId) {
        findPersonById(personId);
        return personRepository.findRolesByPersonId(personId);
    }
}
