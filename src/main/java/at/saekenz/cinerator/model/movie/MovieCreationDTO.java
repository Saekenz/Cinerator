package at.saekenz.cinerator.model.movie;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record MovieCreationDTO(@NotBlank String title,
                               @NotNull @PastOrPresent LocalDate releaseDate,
                               @NotBlank String runtime,
                               @NotBlank @Pattern(regexp = "^tt\\d{6,9}$") String imdbId,
                               @NotNull String posterUrl,
                               @NotEmpty Set<Long> genreIds,
                               @NotEmpty Set<Long> countryIds) {}
