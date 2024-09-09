package at.saekenz.cinerator.model.userlist;

import java.time.LocalDateTime;

public class UserListDTO {

    private Long id;
    private String name;
    private String description;
    private boolean isPrivate;
    private Long userId;
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

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public boolean isPrivate() { return isPrivate; }

    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
