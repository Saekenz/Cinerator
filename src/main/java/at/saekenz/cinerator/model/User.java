package at.saekenz.cinerator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long user_id;
   private String username;
   @Column(length = 100)
   private String password;
   private String role;
   private boolean enabled;

   public User() {
   }

   public User(String username, String password, String role, boolean enabled) {
      this.username = username;
      this.password = password;
      this.role = role;
      this.enabled = enabled;
   }

   public Long getUser_id() {
      return user_id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
