package at.saekenz.cinerator.model.role;

import jakarta.validation.constraints.NotBlank;

public record RoleDTO(Long id,
                      @NotBlank String role) {
}
