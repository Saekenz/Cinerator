package at.saekenz.cinerator.model.country;

import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper implements EntityMapper<Country, CountryDTO> {

    @Override
    public CountryDTO toDTO(Country country) {
        return new CountryDTO(country.getId(),
                country.getName());
    }

    public Country toCountry(CountryDTO countryDTO) {
        return new Country(countryDTO.name());
    }
}
