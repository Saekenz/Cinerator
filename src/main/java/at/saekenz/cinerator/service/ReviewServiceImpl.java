package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public void deleteById(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> findReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> findReviewsLikedByUser(Long userId) {
        return reviewRepository.findLikedByUserId(userId);
    }

    @Override
    public List<Review> findReviewsRatedByUser(Long userId, int rating) {
        return reviewRepository.findRatedByUserId(userId, rating);
    }
}
