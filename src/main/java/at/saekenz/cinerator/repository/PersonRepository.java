package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(p.birthDate = :birthDate OR CAST(:birthDate as timestamp) IS NULL) AND " +
            "(p.deathDate = :deathDate OR CAST(:deathDate as timestamp) IS NULL) AND " +
            "(LOWER(p.height) LIKE LOWER(CONCAT('%', :height, '%')) OR :height IS NULL)")
    List<Person> findPersonsBySearchParams(@Param("name") String name,
                                           @Param("birthDate") LocalDate birthDate,
                                           @Param("deathDate") LocalDate deathDate,
                                           @Param("height") String height);
}
