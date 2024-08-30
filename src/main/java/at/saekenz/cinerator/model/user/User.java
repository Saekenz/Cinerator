package at.saekenz.cinerator.model.user;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long user_id;

   @Column(nullable = false)
   private String username;

   @Column(nullable = false, length = 100)
   private String password;

   @Column(nullable = false)
   private String role;

   @Column(nullable = false)
   private boolean enabled;

   @ManyToMany
   @JoinTable(
           name = "user_watchlist",
           joinColumns = @JoinColumn(name = "user_id"),
           inverseJoinColumns = @JoinColumn(name = "movie_id")
   )
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Set<Movie> watchlist;

   @OneToMany(mappedBy = "user")
   private List<Review> reviews;

   public User() {
   }

   public User(String username, String password, String role, boolean enabled, Set<Movie> watchlist) {
      this.username = username;
      this.password = password;
      this.role = role;
      this.enabled = enabled;
      this.watchlist = watchlist;
   }

   public boolean addMovieToWatchlist(Movie movie) {
      return this.watchlist.add(movie);
   }

   public boolean removeMovieFromWatchlist(Long movie_id) {
      return this.watchlist.removeIf(m -> Objects.equals(m.getMovie_id(), movie_id)); }

   public Long getUser_id() { return user_id; }

   public void setUser_id(Long user_id) { this.user_id = user_id; }

   public String getUsername() { return username; }

   public void setUsername(String username) { this.username = username; }

   public String getPassword() { return password; }

   public void setPassword(String password) { this.password = password; }

   public String getRole() { return role; }

   public void setRole(String role) { this.role = role; }

   public boolean isEnabled() { return enabled; }

   public void setEnabled(boolean enabled) { this.enabled = enabled; }

   public Set<Movie> getWatchlist() { return watchlist; }

   public void setWatchlist(Set<Movie> watchlist) { this.watchlist = watchlist; }

   public List<Review> getReviews() { return reviews; }

   public void setReviews(List<Review> reviews) { this.reviews = reviews; }

   @Override
   public String toString() {
      return "User{" +
              "user_id=" + user_id +
              ", username='" + username + '\'' +
              ", password='" + password + '\'' +
              ", role='" + role + '\'' +
              ", enabled=" + enabled +
              '}';
   }
}
