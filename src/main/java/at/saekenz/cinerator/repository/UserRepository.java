package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) OR :username IS NULL) AND " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')) OR :email IS NULL) AND " +
            "(LOWER(u.role) = LOWER(:role) OR :role IS NULL)")
    List<User> findUsersBySearchParams(@Param("name") String name,
                                       @Param("username") String username,
                                       @Param("email") String email,
                                       @Param("role") String role);

    @Query("SELECT m FROM User u JOIN u.watchlist m WHERE " +
            "u.id = :userId and m.id = :movieId")
    Optional<Movie> findMovieInUsersWatchlist(@Param("userId") Long userId, @Param("movieId") Long movieId);

    @Query("SELECT r FROM User u JOIN u.reviews r WHERE " +
            "u.id = :userId")
    List<Review> findReviewsByUserId(@Param("userId") Long userId);

    @Query("SELECT f.follower FROM User u JOIN u.followers f WHERE " +
            "u.id = :userId")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    @Query("SELECT f.user FROM User u JOIN u.follows f WHERE " +
            "u.id = :userId")
    List<User> findFollowingByUserId(@Param("userId") Long userId);

    @Query("SELECT ul FROM User u JOIN u.userlists ul WHERE " +
            "u.id = :userId")
    List<UserList> findUserListsByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.enabled = TRUE WHERE u.id = :userId")
    int enableUser(@Param("userId") Long userId);

    // TODO: use for findReviews!
//    @Query("SELECT new com.example.dto.ReviewDTO(u.username, r.id, r.isLiked, r.rating, r.reviewDate, m.id, m.title) " +
//            "FROM User u " +
//            "JOIN u.reviews r " +
//            "JOIN r.movie m " +
//            "WHERE u.id = :userId")
//    List<ReviewDTO> fetchReviewDetailsByUserId(@Param("userId") Long userId);

}
