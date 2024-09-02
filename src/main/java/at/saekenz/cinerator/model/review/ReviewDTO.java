package at.saekenz.cinerator.model.review;

import java.time.LocalDate;

public class ReviewDTO {

    private String comment;
    private int rating;
    private LocalDate reviewDate;
    private boolean liked;

    private Long userId;

    public ReviewDTO() {}

    public ReviewDTO(String comment, int rating, LocalDate reviewDate, boolean liked, Long userId) {
        this.comment = comment;
        this.rating = rating;
        this.reviewDate = reviewDate;
        this.liked = liked;
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
