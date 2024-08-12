package at.saekenz.cinerator.model;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long review_id;

    private String comment;
    private int rating;
    private Date review_date;
    private boolean is_liked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public Review() {
    }

    public Review(String comment, int rating, Date review_date, boolean is_liked, User user, Movie movie) {
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

    public Date getReview_date() {
        return review_date;
    }

    public void setReview_date(Date review_date) {
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
}
