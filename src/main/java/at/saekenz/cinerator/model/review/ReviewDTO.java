package at.saekenz.cinerator.model.review;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.Year;

public class ReviewDTO {

    @NotNull
    @Range(min = 1)
    private Long id;

    @NotNull
    @Range(min = 1)
    private Long movieId;

    @NotNull
    private String movieTitle;

    @NotNull
    @PastOrPresent
    private Year movieReleaseYear;

    @NotNull
    @Range(min = 1)
    private Long userId;

    @NotNull
    private String username;

    @NotNull
    @Range(min = 1, max = 5)
    private int rating;

    @NotNull
    private boolean liked;

    @NotNull
    @PastOrPresent
    private LocalDate reviewDate;

    @NotNull
    private String comment;

    public ReviewDTO() {}

    public ReviewDTO(Long id, Long movieId, String movieTitle, Year movieReleaseYear,
                     Long userId, String username, int rating, boolean liked, LocalDate reviewDate,
                     String comment) {
        setId(id);
        setUserId(userId);
        setMovieId(movieId);
        setMovieReleaseYear(movieReleaseYear);
        setMovieTitle(movieTitle);
        setUsername(username);
        setRating(rating);
        setReviewDate(reviewDate);
        setLiked(liked);
        setComment(comment);
    }

    public ReviewDTO(Long id, Long movieId, String movieTitle, int movieReleaseYear,
                     Long userId, String username, int rating, boolean liked, LocalDate reviewDate,
                     String comment) {
        setId(id);
        setUserId(userId);
        setMovieId(movieId);
        setMovieReleaseYear(Year.of(movieReleaseYear));
        setMovieTitle(movieTitle);
        setUsername(username);
        setRating(rating);
        setReviewDate(reviewDate);
        setLiked(liked);
        setComment(comment);
    }



    public @NotNull @Range(min = 1) Long getId() {
        return id;
    }

    public void setId(@NotNull @Range(min = 1) Long id) {
        this.id = id;
    }

    public @NotNull String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(@NotNull String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public @NotNull @PastOrPresent Year getMovieReleaseYear() {
        return movieReleaseYear;
    }

    public void setMovieReleaseYear(@NotNull @PastOrPresent Year movieReleaseYear) {
        this.movieReleaseYear = movieReleaseYear;
    }

    public @NotNull @Range(min = 1) Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull @Range(min = 1) Long userId) {
        this.userId = userId;
    }

    public @NotNull String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
    }

    @NotNull
    @Range(min = 1, max = 5)
    public int getRating() {
        return rating;
    }

    public void setRating(@NotNull @Range(min = 1, max = 5) int rating) {
        this.rating = rating;
    }

    @NotNull
    public boolean isLiked() {
        return liked;
    }

    public void setLiked(@NotNull boolean liked) {
        this.liked = liked;
    }

    public @NotNull @PastOrPresent LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(@NotNull @PastOrPresent LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public @NotNull String getComment() {
        return comment;
    }

    public void setComment(@NotNull String comment) {
        this.comment = comment;
    }

    public @NotNull @Range(min = 1) Long getMovieId() {
        return movieId;
    }

    public void setMovieId(@NotNull @Range(min = 1) Long movieId) {
        this.movieId = movieId;
    }
}
