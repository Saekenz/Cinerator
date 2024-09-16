package at.saekenz.cinerator.model.person;

import java.time.LocalDate;

public class PersonDTO {

    private Long id;
    private String name;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String birthCountry;

    public PersonDTO() {}

    public PersonDTO(Long id, String name,
                     LocalDate birthDate, LocalDate deathDate,
                     String birthCountry) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
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

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }
}
