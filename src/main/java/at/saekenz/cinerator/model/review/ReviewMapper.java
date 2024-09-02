package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setComment(review.getComment());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setReviewDate(review.getReviewDate());
        reviewDTO.setLiked(review.isLiked());
        reviewDTO.setUserId(review.getUserId());

        return reviewDTO;
    }

    public Review toReview(ReviewDTO reviewDTO, User user, Movie movie) {
        Review review = new Review();

        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setReviewDate(reviewDTO.getReviewDate());
        review.setIsLiked(reviewDTO.isLiked());
        review.setUser(user);
        review.setMovie(movie);

        return review;
    }
}
