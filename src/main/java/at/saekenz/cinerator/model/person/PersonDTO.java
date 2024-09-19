package at.saekenz.cinerator.model.person;

import at.saekenz.cinerator.model.country.CountryDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

public class PersonDTO {

    private Long id;

    @NotBlank(message = "A valid (full) name is required.")
    private String name;

    @NotNull(message = "The date of birth is required.")
    @Past(message = "The date of birth has to be in the past.")
    private LocalDate birthDate;

    @PastOrPresent(message = "The date of death has to be today or in the past.")
    private LocalDate deathDate;

    private int age;

    @Valid
    @NotNull(message = "The country of birth is required.")
    private CountryDTO birthCountry;

    public PersonDTO() {}

    public PersonDTO(Long id, String name,
                     LocalDate birthDate, LocalDate deathDate,
                     CountryDTO birthCountry) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        calculateAge();
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

    public int getAge() {
        return age;
    }

    private void calculateAge() {
        this.age = Period.between(birthDate, Objects.requireNonNullElseGet(deathDate, LocalDate::now)).getYears();
    }

    public CountryDTO getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(CountryDTO birthCountry) {
        this.birthCountry = birthCountry;
    }

    @JsonIgnore
    public String getBirthCountryName() {
        return this.birthCountry.name();
    }
}
