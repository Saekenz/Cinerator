package at.saekenz.cinerator.model.castinfo;

public class CastInfoDTO {

    private Long id;
    private Long movieId;
    private Long personId;
    private Long roleId;
    private String characterName;

    public CastInfoDTO() {}

    public CastInfoDTO(Long id, Long movieId,
                       Long personId, Long roleId) {
        this.id = id;
        this.movieId = movieId;
        this.personId = personId;
        this.roleId = roleId;
    }

    public CastInfoDTO(Long id, Long movieId, Long personId,
                       Long roleId, String characterName) {
        this.id = id;
        this.movieId = movieId;
        this.personId = personId;
        this.roleId = roleId;
        this.characterName = characterName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
