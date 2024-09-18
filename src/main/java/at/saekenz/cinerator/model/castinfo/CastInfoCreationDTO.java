package at.saekenz.cinerator.model.castinfo;

public class CastInfoCreationDTO {

    private Long movieId;
    private Long personId;
    private Long roleId;
    private String characterName;

    public CastInfoCreationDTO() {}

    public CastInfoCreationDTO(Long movieId, Long personId, Long roleId, String characterName) {
        this.movieId = movieId;
        this.personId = personId;
        this.roleId = roleId;
        this.characterName = characterName;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
