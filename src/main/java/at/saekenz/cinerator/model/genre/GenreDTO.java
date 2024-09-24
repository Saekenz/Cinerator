package at.saekenz.cinerator.model.genre;

import jakarta.validation.constraints.NotBlank;

public record GenreDTO(Long id,
                       @NotBlank String name) {
}
