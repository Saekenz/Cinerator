package at.saekenz.cinerator.model.genre;

import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreDTO toDTO(Genre genre) {
        return new GenreDTO(genre.getId(),
                genre.getName());
    }

    public Genre toGenre(GenreDTO genreDTO) {
        return new Genre(genreDTO.name());
    }
}
