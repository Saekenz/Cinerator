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
import java.util.Objects;

@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String runtime;

    @Column(nullable = false)
    private String director;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String imdbId;

    private String posterUrl;

    @ManyToMany(mappedBy = "watchlist")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<User> user;

    @OneToMany(mappedBy = "movie")
    private List<Review> reviews;

    @ManyToMany
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "MOV_ID"),
            inverseJoinColumns = @JoinColumn(name = "ACT_ID"))
    private List<Actor> actors; // ok if actors contained multiple times -> multiple roles! (impl in future)

    public Movie() {

    }

    public Movie(String title, String director, LocalDate releaseDate, String runtime,
                 String genre, String country, String imdbId, String posterUrl) {
        this.title = title;
        this.director = director;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.genre = genre;
        this.country = country;
        this.imdbId = imdbId;
        this.posterUrl = posterUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getReleaseYear() {
        return posterUrl != null ? releaseDate.getYear() : 0;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntime() { return runtime; }

    public void setRuntime(String runtime) { this.runtime = runtime; }

    public String getDirector() { return director; }

    public void setDirector(String director) { this.director = director; }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public String getCountry() {return country; }

    public void setCountry(String country) { this.country = country; }

    public String getImdbId() { return imdbId; }

    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public String getPosterUrl() { return posterUrl; }

    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public List<User> getUser() { return user; }

    public void setUser(List<User> user) { this.user = user; }

    public List<Review> getReviews() { return reviews; }

    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public List<Actor> getActors() { return actors; }

    public void setActors(List<Actor> actors) { this.actors = actors; }

    public void addActor(Actor actor) { this.actors.add(actor); }

    public void removeActor(Long id) { this.actors.removeIf(a -> Objects.equals(a.getId(), id)); }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", runtime=" + runtime +
                ", director='" + director + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }
}
