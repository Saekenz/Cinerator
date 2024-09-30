package at.saekenz.cinerator.model.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Range;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;

public record ReviewCreationDTO(@NotNull @PastOrPresent LocalDate reviewDate,
                                @NotNull @DefaultValue("") String comment,
                                @NotNull @Range(min = 1, max = 5) int rating,
                                @NotNull boolean isLiked,
                                @NotNull @Range(min = 1) Long userId) {
}
