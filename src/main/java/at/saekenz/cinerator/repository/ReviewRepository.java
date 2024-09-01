package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    List<Review> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.isLiked = true")
    List<Review> findLikedByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.rating = :rating")
    List<Review> findRatedByUserId(@Param("userId") Long userId, @Param("rating") int rating);
}
