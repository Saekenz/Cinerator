package at.saekenz.cinerator.model.country;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "countries")
    private Set<Movie> movies;

    @OneToMany(mappedBy = "birthCountry")
    private Set<Person> persons;

    public Country() {}

    public Country(String name) {
        this.name = name;
    }

    public Country(String name,
                   Set<Movie> movies,
                   Set<Person> persons) {
        this.name = name;
        this.movies = movies;
        this.persons = persons;
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

    public Set<Person> getPersons() {
        return persons;
    }

    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }
}
