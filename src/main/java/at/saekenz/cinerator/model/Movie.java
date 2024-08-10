package at.saekenz.cinerator.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long movie_id;

    private String title;
    private Date release_date;
    private String director;
    private String genre;
    private String country;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    public Movie() {

    }

    public Movie(String title, String director, Date release_date, String genre,
                 String country, List<Review> reviews) {
        this.title = title;
        this.director = director;
        this.release_date = release_date;
        this.genre = genre;
        this.country = country;
        this.reviews = reviews;
    }

    public Long getMovie_id() {
        return movie_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
