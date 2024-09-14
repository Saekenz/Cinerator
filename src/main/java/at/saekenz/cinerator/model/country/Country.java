package at.saekenz.cinerator.model.country;

import at.saekenz.cinerator.model.movie.Movie;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(mappedBy = "countries")
    private Set<Movie> movies;

    public Country() {}

    public Country(String name) {
        this.name = name;
    }

    @PreRemove
    private void removeGenreFromMovies() {
        for (Movie movie : movies) {
            movie.getCountries().remove(this);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Movie> getMovies() {
        return movies;
    }

    public void setMovies(Set<Movie> movies) {
        this.movies = movies;
    }
}
