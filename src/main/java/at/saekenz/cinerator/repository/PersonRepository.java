package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
