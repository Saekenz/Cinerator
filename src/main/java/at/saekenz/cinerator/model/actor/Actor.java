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
    private Long actor_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth_date;

    @Column(nullable = false)
    private String birth_country;

    private int age;

    @ManyToMany(mappedBy = "actors")
    @JsonIgnore
    private List<Movie> movies;

    public Actor() {}

    public Actor(String name, LocalDate birth_date, String birth_country) {
        this.name = name;
        this.birth_date = birth_date;
        this.birth_country = birth_country;
        this.age = Period.between(birth_date, LocalDate.now()).getYears();
    }

    @PreRemove
    private void removeActorFromMovies() {
        for (Movie movie : movies) {
            movie.getActors().remove(this);
        }
    }

    public void setActor_id(Long actorId) {
        this.actor_id = actorId;
    }

    public Long getActor_id() {
        return actor_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public String getBirth_country() {
        return birth_country;
    }

    public void setBirth_country(String birth_country) {
        this.birth_country = birth_country;
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
                "actor_id=" + actor_id +
                ", name='" + name + '\'' +
                ", birth_date=" + birth_date +
                ", birth_country='" + birth_country + '\'' +
                ", age=" + age +
                '}';
    }
}
