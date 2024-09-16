package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.person.Person;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    List<Person> findAll();

    Optional<Person> findById(Long id);

    Person getReferenceById(Long id);

    Person save(Person person);

    void deleteById(Long id);
}
