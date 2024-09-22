package at.saekenz.cinerator.model.person;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.country.Country;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column
    private LocalDate deathDate;

    @Column(nullable = false)
    private String height;

    @ManyToOne
    @JoinColumn(name = "COU_ID")
    private Country birthCountry;

    @OneToMany(mappedBy = "person")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<CastInfo> castInfos;

    public Person() {}

    public Person(String name, LocalDate birthDate, LocalDate deathDate, String height) {
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.height = height;
    }

    public Person(String name, LocalDate birthDate, LocalDate deathDate, String height, Country birthCountry) {
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.height = height;
        this.birthCountry = birthCountry;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Country getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(Country birthCountry) {
        this.birthCountry = birthCountry;
    }

    public Set<CastInfo> getCastInfos() {
        return castInfos;
    }

    public void setCastInfos(Set<CastInfo> castInfos) {
        this.castInfos = castInfos;
    }
}
