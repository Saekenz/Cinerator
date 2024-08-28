package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movie_id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate release_date;

    @Column(nullable = false)
    private String runtime;

    @Column(nullable = false)
    private String director;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String imdb_id;

    private String poster_url;

    @ManyToMany(mappedBy = "watchlist")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<User> user;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private List<Actor> actors;

    public Movie() {

    }

    public Movie(String title, String director, LocalDate release_date, String runtime,
                 String genre, String country, String imdb_id, String poster_url) {
        this.title = title;
        this.director = director;
        this.release_date = release_date;
        this.runtime = runtime;
        this.genre = genre;
        this.country = country;
        this.imdb_id = imdb_id;
        this.poster_url = poster_url;
    }

    public Long getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(Long movie_id) { this.movie_id = movie_id; }

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
        return poster_url != null ? release_date.getYear() : 0;
    }

    public void setRelease_date(LocalDate release_date) {
        this.release_date = release_date;
    }

    public String getRuntime() { return runtime; }

    public void setRuntime(String runtime) { this.runtime = runtime; }

    public String getDirector() { return director; }

    public void setDirector(String director) { this.director = director; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public String getCountry() {return country; }

    public void setCountry(String country) { this.country = country; }

    public String getImdb_id() { return imdb_id; }

    public void setImdb_id(String imdb_id) { this.imdb_id = imdb_id; }

    public String getPoster_url() { return poster_url; }

    public void setPoster_url(String poster_url) { this.poster_url = poster_url; }

    public List<User> getUser() { return user; }

    public void setUser(List<User> user) { this.user = user; }

    public List<Review> getReviews() { return reviews; }

    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public List<Actor> getActors() { return actors; }

    public void setActors(List<Actor> actors) { this.actors = actors; }

    @Override
    public String toString() {
        return "Movie{" +
                "movie_id=" + movie_id +
                ", title='" + title + '\'' +
                ", release_date=" + release_date +
                ", runtime=" + runtime +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", imdb_id='" + imdb_id + '\'' +
                ", poster_url='" + poster_url + '\'' +
                '}';
    }
}
