package at.saekenz.cinerator.model.actor;

import at.saekenz.cinerator.model.movie.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "actors")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private String birthCountry;

    private int age;

    @ManyToMany(mappedBy = "actors")
    @JsonIgnore
    private List<Movie> movies;

    public Actor() {}

    public Actor(String name, LocalDate birthDate, String birthCountry) {
        this.name = name;
        this.birthDate = birthDate;
        this.birthCountry = birthCountry;
        this.age = Period.between(birthDate, LocalDate.now()).getYears();
    }

    @PreRemove
    private void removeActorFromMovies() {
        for (Movie movie : movies) {
            movie.getActors().remove(this);
        }
    }

    public void setId(Long actorId) {
        this.id = actorId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Movie> getMovies() { return movies; }

    public void setMovies(List<Movie> movies) { this.movies = movies; }

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", birthCountry='" + birthCountry + '\'' +
                ", age=" + age +
                '}';
    }


}
