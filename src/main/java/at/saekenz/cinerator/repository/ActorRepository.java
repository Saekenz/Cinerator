package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.actor.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {

    List<Actor> findByName(String name);

    List<Actor> findByAge(int age);

    List<Actor> findByNameAndAge(String name, int age);

    @Query("SELECT a FROM Actor a WHERE a.birthDate = :birthDate")
    List<Actor> findByBirthDate(@Param("birthDate") LocalDate birthDate);

    @Query("SELECT a FROM Actor a WHERE a.birthCountry = :country")
    List<Actor> findByBirthCountry(@Param("country") String country);

    @Query("SELECT a FROM Actor a WHERE EXTRACT(YEAR FROM a.birthDate) = :birthYear")
    List<Actor> findByBirthYear(@Param("birthYear") int birthYear);

    @Query("SELECT a FROM Actor a WHERE " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(a.birthDate = :birthDate OR CAST(:birthDate as timestamp) IS NULL) AND " +
            "(LOWER(a.birthCountry) = LOWER(:birthCountry) OR :birthCountry IS NULL) AND " +
            "(a.age = :age OR :age IS NULL)")
    List<Actor> findActorsBySearchParams(@Param("name") String name,
                                         @Param("birthDate") LocalDate birthDate,
                                         @Param("birthCountry") String birthCountry,
                                         @Param("age") Integer age);
}
