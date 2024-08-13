package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.Review;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long movie_id;

    private String title;
    private LocalDate release_date;
    private String director;
    private String genre;
    private String country;
    private String imdb_id;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    public Movie() {

    }

    public Movie(String title, String director, LocalDate release_date, String genre,
                 String country, String imdb_id, List<Review> reviews) {
        this.title = title;
        this.director = director;
        this.release_date = release_date;
        this.genre = genre;
        this.country = country;
        this.imdb_id = imdb_id;
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

    public LocalDate getRelease_date() {
        return release_date;
    }

    public int getReleaseYear() {
        return release_date.getYear();
    }

    public void setRelease_date(LocalDate release_date) {
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

    public String getImdb_id() { return imdb_id; }

    public void setImdb_id(String imdb_id) { this.imdb_id = imdb_id; }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", title='" + title + '\'' +
                ", release_date=" + release_date +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", imdb_id='" + imdb_id + '\'' +
                ", reviews=" + reviews +
                '}';
    }
}
