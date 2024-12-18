package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {

    // Cast needed to make age = null possible
    @Query("SELECT p FROM Person p JOIN p.birthCountry c WHERE " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(p.birthDate = :birthDate OR CAST(:birthDate as timestamp) IS NULL) AND " +
            "(p.deathDate = :deathDate OR CAST(:deathDate as timestamp) IS NULL) AND " +
            "(LOWER(p.height) LIKE LOWER(CONCAT('%', :height, '%')) OR :height IS NULL) AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :country, '%')) OR :country IS NULL) AND " +
            "(CAST(DATE_PART('year', AGE(COALESCE(p.deathDate, CURRENT_DATE), p.birthDate)) AS INTEGER) = :age OR :age IS NULL)")
    List<Person> findPersonsBySearchParams(@Param("name") String name,
                                           @Param("birthDate") LocalDate birthDate,
                                           @Param("deathDate") LocalDate deathDate,
                                           @Param("height") String height,
                                           @Param("country") String country,
                                           @Param("age") Integer age);

    @Query("SELECT c.movie FROM CastInfo c WHERE " +
            "(c.person.id = :personId) AND " +
            "(LOWER(c.role.role) = LOWER(:role) OR :role IS NULL)")
    List<Movie> findMoviesByPersonIdAndRole(@Param("personId") Long personId,
                                            @Param("role") String role);

    @Query("SELECT c FROM CastInfo c WHERE " +
            "c.person.id = :personId")
    List<CastInfo> findCastInfosByPersonId(Long personId);

    @Query("SELECT DISTINCT c.role FROM CastInfo c WHERE " +
            "c.person.id = :personId")
    List<Role> findRolesByPersonId(Long personId);
}
