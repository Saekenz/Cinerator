package at.saekenz.cinerator.model.userlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

public class UserListDTO {

    private Long id;

    @NotBlank(message = "A valid name is required.")
    private String name;

    private String description;

    @NotNull(message = "A valid privacy status is required.")
    private boolean isPrivate;

    @NotNull(message = "A valid user is required.")
    @Range(min = 1)
    private Long userId;

    @NotNull(message = "A valid date is required.")
    @PastOrPresent
    private LocalDateTime createdAt;

    public UserListDTO() {}

    public UserListDTO(Long id, String name, String description, boolean isPrivate, Long userId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public void setId(@NotNull @Range(min = 1) Long id) {
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isPrivate() { return isPrivate; }

    public void setPrivate(@NotNull boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Long getUserId() { return userId; }

    public void setUserId(@NotNull @Range(min = 1) Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(@NotNull @PastOrPresent LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
