package at.saekenz.cinerator.model.country;

import jakarta.validation.constraints.NotBlank;

public record CountryDTO(Long id,
                         @NotBlank String name) {
}
