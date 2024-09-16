package at.saekenz.cinerator.model.castinfo;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import jakarta.persistence.*;

@Entity
@Table(name = "castinfos")
public class CastInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "MOV_ID")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "PER_ID")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @Column
    private String characterName;

    public CastInfo() {}

    public CastInfo(Movie movie, Person person, Role role) {
        this.movie = movie;
        this.person = person;
        this.role = role;
    }

    public CastInfo(Movie movie, Person person, Role role, String characterName) {
        this.movie = movie;
        this.person = person;
        this.role = role;
        this.characterName = characterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
