package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private String imdbId;

    private String posterUrl;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "MOV_ID"),
            inverseJoinColumns = @JoinColumn(name = "GEN_ID"))
    private Set<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "movie_countries",
            joinColumns = @JoinColumn(name = "MOV_ID"),
            inverseJoinColumns = @JoinColumn(name = "COU_ID")
    )
    private Set<Country> countries;

    @ManyToMany(mappedBy = "watchlist")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<User> user;

    @ManyToMany(mappedBy = "movielist")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserList> userlist;

    @OneToMany(mappedBy = "movie")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Review> reviews;

    @OneToMany(mappedBy = "movie")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<CastInfo> castInfos;

    public Movie() {

    }

    public Movie(String title, LocalDate releaseDate, String runtime,
                 String imdbId, String posterUrl) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
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
        return releaseDate != null ? releaseDate.getYear() : 0;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRuntime() { return runtime; }

    public void setRuntime(String runtime) { this.runtime = runtime; }

    public String getImdbId() { return imdbId; }

    public void setImdbId(String imdbId) { this.imdbId = imdbId; }

    public String getPosterUrl() { return posterUrl; }

    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public List<User> getUser() { return user; }

    public void setUser(List<User> user) { this.user = user; }

    public List<Review> getReviews() { return reviews; }

    public void setReviews(List<Review> reviews) { this.reviews = reviews; }

    public Set<Genre> getGenres() { return genres; }

    public void setGenres(Set<Genre> genres) { this.genres = genres; }

    public Set<Country> getCountries() { return countries; }

    public void setCountries(Set<Country> countries) { this.countries = countries; }

    public List<UserList> getUserlist() { return userlist; }

    public void setUserlist(List<UserList> userlist) { this.userlist = userlist; }

    public Set<CastInfo> getCastInfos() { return castInfos; }

    public void setCastInfos(Set<CastInfo> castInfos) { this.castInfos = castInfos; }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", runtime='" + runtime + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", genres=" + genres +
                ", countries=" + countries +
                ", user=" + user +
                ", userlist=" + userlist +
                ", reviews=" + reviews +
                ", castInfos=" + castInfos +
                '}';
    }

    public boolean removeReview(Long reviewId) {
        return this.reviews.removeIf(r -> r.getId().equals(reviewId));
    }


}
