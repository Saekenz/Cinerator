package at.saekenz.cinerator.model.user;

public class UserDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String bio;
    private String role;
    private boolean enabled;

    public UserDTO() {}

    public UserDTO(Long id, String username, String name, String email,
                   String bio, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }

    public void setBio(String bio) { this.bio = bio; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
