package at.saekenz.cinerator.model.userlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record UserListCreationDTO(@NotBlank String name,
                                  String description,
                                  @NotNull boolean isPrivate,
                                  @Range(min = 1) Long userId) {

}
