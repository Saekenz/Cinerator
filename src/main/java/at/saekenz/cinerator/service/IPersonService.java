package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.person.Person;

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
}
