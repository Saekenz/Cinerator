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

    @Query("SELECT a FROM Actor a WHERE a.birth_date = :birth_date")
    List<Actor> findByBirthDate(@Param("birth_date") LocalDate birth_date);

    @Query("SELECT a FROM Actor a WHERE a.birth_country = :country")
    List<Actor> findByBirthCountry(@Param("country") String country);

    @Query("SELECT a FROM Actor a WHERE EXTRACT(YEAR FROM a.birth_date) = :birth_year")
    List<Actor> findByBirthYear(@Param("birth_year") int birth_year);
}
