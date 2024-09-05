package at.saekenz.cinerator.model.user;

import at.saekenz.cinerator.model.follow.Follow;
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
   private Long id;

   @Column(nullable = false)
   private String username;

   @Column(nullable = false)
   private String name;

   @Column(nullable = false, length = 100)
   private String password;

   @Column(nullable = false)
   private String email;

   @Column(nullable = false)
   private String bio;

   @Column(nullable = false)
   private String role;

   @Column(nullable = false)
   private boolean enabled;

   @ManyToMany
   @JoinTable(
           name = "user_watchlist",
           joinColumns = @JoinColumn(name = "USR_ID"),
           inverseJoinColumns = @JoinColumn(name = "MOV_ID")
   )
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Set<Movie> watchlist;

   @OneToMany(mappedBy = "user")
   private List<Review> reviews;

   @OneToMany(mappedBy = "follower")
   private Set<Follow> follows;

   @OneToMany(mappedBy = "user")
   private Set<Follow> followers;

   public User() {
   }

   public User(String username, String name, String password,
               String email, String bio, String role,
               boolean enabled, Set<Movie> watchlist) {
      this.username = username;
      this.name = name;
      this.password = password;
      this.email = email;
      this.bio = bio;
      this.role = role;
      this.enabled = enabled;
      this.watchlist = watchlist;
   }

   public boolean addMovieToWatchlist(Movie movie) {
      return this.watchlist.add(movie);
   }

   public boolean removeMovieFromWatchlist(Long movieId) {
      return this.watchlist.removeIf(m -> Objects.equals(m.getId(), movieId)); }

   public Long getId() { return id; }

   public void setId(Long id) { this.id = id; }

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

   public String getName() { return name; }

   public void setName(String name) { this.name = name; }

   public String getEmail() { return email; }

   public void setEmail(String email) { this.email = email; }

   public String getBio() { return bio; }

   public void setBio(String bio) { this.bio = bio; }

   public Set<Follow> getFollows() { return follows; }

   public void setFollows(Set<Follow> follows) { this.follows = follows; }

   public Set<Follow> getFollowers() { return followers; }

   public void setFollowers(Set<Follow> followers) { this.followers = followers; }

   public void addFollower(Follow follower) {
      this.followers.add(follower);
   }

   public void removeFollower(Follow follower) {
      this.followers.remove(follower);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", password='" + password + '\'' +
              ", role='" + role + '\'' +
              ", enabled=" + enabled +
              '}';
   }
}
