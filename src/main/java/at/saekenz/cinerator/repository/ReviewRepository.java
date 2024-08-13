package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.user.user_id = :user_id")
    List<Review> findByUserId(@Param("user_id") Long user_id);

    @Query("SELECT r FROM Review r WHERE r.user.user_id = :user_id AND r.is_liked = true")
    List<Review> findLikedByUserId(@Param("user_id") Long user_id);

    @Query("SELECT r FROM Review r WHERE r.user.user_id = :user_id AND r.rating = :rating")
    List<Review> findRatedByUserId(@Param("user_id") Long user_id, @Param("rating") int rating);
}
