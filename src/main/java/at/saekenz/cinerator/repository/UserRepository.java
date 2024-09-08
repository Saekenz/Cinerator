package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%'))")
    List<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    User getUserByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE LOWER(u.role) = LOWER(:role)")
    List<User> findUsersByRole(@Param("role") String role);

    @Query("SELECT m " +
            "FROM Movie m " +
            "INNER JOIN Review r ON m.id = r.movie.id " +
            "INNER JOIN User u ON u.id = r.user.id " +
            "WHERE u.id = :id " +
            "AND r.isLiked = true")
    List<Movie> findMoviesLikedByUser(@Param("id") Long id);

    @Query("SELECT m " +
            "FROM Movie m " +
            "INNER JOIN Review r ON m.id = r.movie.id " +
            "INNER JOIN User u ON u.id = r.user.id " +
            "WHERE u.id = :userId " +
            "AND r.rating = :rating")
    List<Movie> findMoviesRatedByUser(@Param("userId") Long userId, @Param("rating") Integer rating);
}
