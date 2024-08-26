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

    @Query("SELECT a FROM Actor a WHERE " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(a.birth_date = :birth_date OR :birth_date IS NULL) AND " +
            "(LOWER(a.birth_country) = LOWER(:birth_country) OR :birth_country IS NULL) AND " +
            "(a.age = :age OR :age IS NULL)")
    List<Actor> findActorsBySearchParams(@Param("name") String name,
                                         @Param("birth_date") LocalDate birth_date,
                                         @Param("birth_country") String birth_country,
                                         @Param("age") Integer age);
}
