package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.role.Role;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPersonService {

    List<Person> findAll();

    Optional<Person> findById(Long id);

    Person getReferenceById(Long id);

    Person save(Person person);

    void deleteById(Long id);

    List<Person> findPersonsBySearchParams(String name, LocalDate birthDate, LocalDate deathDate, String height,
                                           String country, Integer age);

    List<Movie> findMoviesByPersonIdAndRole(Long personId, String role);

    Page<Person> findAllPaged(int page, int size, String sortField, String sortDirection);

    Country findCountryByPersonId(Long personId);

    Person updatePerson(Long id, PersonDTO personDTO);

    Person createPerson(PersonDTO personDTO);

    Person findPersonById(Long id);

    List<CastInfo> findCastInfosByPersonId(Long personId);

    List<Role> findRolesByPersonId(Long personId);
}
