package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewDTO;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Page<Review> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return reviewRepository.findAll(pageable);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Optional<ReviewDTO> findDTOById(Long id) {
        return reviewRepository.findDTOById(id);
    }

    @Override
    public Review findReviewById(Long id) {
        return reviewRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Review with id %d could not be found!", id)));
    }

    @Override
    public ReviewDTO findReviewDTOById(Long id) {
        return findDTOById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Review with id %d could not be found!", id)));
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
    public User findUserByReviewId(Long reviewId) {
        return findReviewById(reviewId).getUser();
    }

    @Override
    public Movie findMovieByReviewId(Long reviewId) {
        return findReviewById(reviewId).getMovie();
    }
}
