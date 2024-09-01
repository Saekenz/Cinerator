package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String comment;
    private int rating;

    @Column(nullable = false)
    private LocalDate reviewDate;

    private boolean isLiked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USR_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference(value = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MOV_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference(value = "movie_id")
    private Movie movie;

    public Review() {
    }

    public Review(String comment, int rating, LocalDate reviewDate, boolean isLiked, User user, Movie movie) {
        this.comment = comment;
        this.rating = rating;
        this.reviewDate = reviewDate;
        this.isLiked = isLiked;
        this.user = user;
        this.movie = movie;
    }

    public Long getId() {
        return id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public String getUsername() { return user.getUsername(); }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", reviewDate=" + reviewDate +
                ", isLiked=" + isLiked +
                '}';
    }
}
