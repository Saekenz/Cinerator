package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.review.Review;

import java.util.List;
import java.util.Optional;

public interface IReviewService {

    List<Review> findAll();

    Optional<Review> findById(Long id);

    Review save(Review review);

    void deleteById(Long id);

    List<Review> findReviewsByUser(Long userId);

    List<Review> findReviewsLikedByUser(Long userId);

    List<Review> findReviewsRatedByUser(Long userId, int rating);
}
