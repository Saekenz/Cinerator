package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT new at.saekenz.cinerator.model.review.ReviewDTO(r.id, m.id, m.title, " +
            "EXTRACT(YEAR FROM(m.releaseDate)), u.id, u.username, r.rating, r.isLiked, r.reviewDate, r.comment) " +
            "FROM User u " +
            "JOIN u.reviews r " +
            "JOIN r.movie m " +
            "WHERE r.id = :id")
    public Optional<ReviewDTO> findDTOById(Long id);
}
