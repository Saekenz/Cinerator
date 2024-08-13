package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long review_id;

    private String comment;
    private int rating;
    private LocalDate review_date;
    private boolean is_liked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private Movie movie;

    public Review() {
    }

    public Review(String comment, int rating, LocalDate review_date, boolean is_liked, User user, Movie movie) {
        this.comment = comment;
        this.rating = rating;
        this.review_date = review_date;
        this.is_liked = is_liked;
        this.user = user;
        this.movie = movie;
    }

    public Long getReview_id() {
        return review_id;
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

    public LocalDate getReview_date() {
        return review_date;
    }

    public void setReview_date(LocalDate review_date) {
        this.review_date = review_date;
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

    public boolean isIs_liked() {
        return is_liked;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    @Override
    public String toString() {
        return "Review{" +
                "review_id=" + review_id +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", review_date=" + review_date +
                ", is_liked=" + is_liked +
                '}';
    }
}
