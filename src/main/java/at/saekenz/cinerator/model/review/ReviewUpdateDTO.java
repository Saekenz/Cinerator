package at.saekenz.cinerator.model.review;

import java.util.Objects;

public class ReviewUpdateDTO {

    private String comment;
    private int rating;
    private boolean liked;

    public ReviewUpdateDTO() {}

    public ReviewUpdateDTO(String comment, int rating, boolean liked) {
        this.comment = Objects.requireNonNull(comment, "comment must not be null");
        this.rating = rating;
        this.liked = liked;
    }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public boolean isLiked() { return liked; }

    public void setLiked(boolean liked) { this.liked = liked; }
}
