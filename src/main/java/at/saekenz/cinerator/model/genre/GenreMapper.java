package at.saekenz.cinerator.model.genre;

import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper implements EntityMapper<Genre, GenreDTO> {

    public GenreDTO toDTO(Genre genre) {
        return new GenreDTO(genre.getId(),
                genre.getName());
    }

    public Genre toGenre(GenreDTO genreDTO) {
        return new Genre(genreDTO.name());
    }
}
