package at.saekenz.cinerator.model.castinfo;

import at.saekenz.cinerator.model.movie.MovieDTO;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.role.RoleDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CastInfoDTO {

    private Long id;
    private MovieDTO movieDTO;
    private PersonDTO personDTO;
    private RoleDTO roleDTO;
    private String characterName;

    public CastInfoDTO() {}

    public CastInfoDTO(Long id, MovieDTO movieDTO,
                       PersonDTO personDTO, RoleDTO roleDTO) {
        this.id = id;
        this.movieDTO = movieDTO;
        this.personDTO = personDTO;
        this.roleDTO = roleDTO;
    }

    public CastInfoDTO(Long id, MovieDTO movieDTO, PersonDTO personDTO,
                       RoleDTO roleDTO, String characterName) {
        this.id = id;
        this.movieDTO = movieDTO;
        this.personDTO = personDTO;
        this.roleDTO = roleDTO;
        this.characterName = characterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovieDTO getMovieDTO() {
        return movieDTO;
    }

    public void setMovieDTO(MovieDTO movieDTO) {
        this.movieDTO = movieDTO;
    }

    @JsonIgnore
    public Long getMovieId() {
        return movieDTO.getId();
    }

    public PersonDTO getPersonDTO() {
        return personDTO;
    }

    public void setPersonDTO(PersonDTO personDTO) {
        this.personDTO = personDTO;
    }

    @JsonIgnore
    public Long getPersonId() {
        return personDTO.getId();
    }

    public RoleDTO getRoleDTO() {
        return roleDTO;
    }

    public void setRoleDTO(RoleDTO roleDTO) {
        this.roleDTO = roleDTO;
    }

    @JsonIgnore
    public Long getRoleId() {
        return roleDTO.id();
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
