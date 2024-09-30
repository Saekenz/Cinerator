package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewDTO;
import at.saekenz.cinerator.model.user.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IReviewService {

    List<Review> findAll();

    Page<Review> findAllPaged(int page, int size, String sortField, String sortDirection);

    Optional<Review> findById(Long id);

    Optional<ReviewDTO> findDTOById(Long id);

    Review findReviewById(Long id);

    ReviewDTO findReviewDTOById(Long id);

    Review save(Review review);

    void deleteById(Long id);

    User findUserByReviewId(Long reviewId);

    Movie findMovieByReviewId(Long reviewId);

}
