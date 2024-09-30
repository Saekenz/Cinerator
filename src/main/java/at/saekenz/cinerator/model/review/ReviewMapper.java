package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper implements EntityMapper<Review, ReviewDTO> {

    public ReviewDTO toDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();

        reviewDTO.setId(review.getId());
        reviewDTO.setMovieId(review.getMovieId());
        reviewDTO.setMovieTitle(review.getMovieTitle());
        reviewDTO.setMovieReleaseYear(review.getMovieYear());
        reviewDTO.setUserId(review.getUserId());
        reviewDTO.setUsername(review.getUsername());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setLiked(review.isLiked());
        reviewDTO.setReviewDate(review.getReviewDate());
        reviewDTO.setComment(review.getComment());

        return reviewDTO;
    }

    public Review toReview(ReviewCreationDTO creationDTO) {
        Review review = new Review();

        review.setReviewDate(creationDTO.reviewDate());
        review.setComment(creationDTO.comment());
        review.setRating(creationDTO.rating());
        review.setIsLiked(creationDTO.isLiked());

        return review;
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
