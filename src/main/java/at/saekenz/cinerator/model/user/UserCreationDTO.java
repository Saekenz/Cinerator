package at.saekenz.cinerator.model.user;

import jakarta.validation.constraints.NotBlank;

public class UserCreationDTO {

    @NotBlank(message = "A valid email address is required.")
    private String email;

    @NotBlank(message = "A valid username is required.")
    private String username;

    @NotBlank(message = "A valid password is required.")
    private String password;

    public UserCreationDTO() {}

    public UserCreationDTO(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String toString() {
        return "UserCreationDTO [email=" + email + ", username=" + username + ", password=" + password + "]";
    }
}
