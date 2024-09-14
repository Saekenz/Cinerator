package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.country.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
