package at.saekenz.cinerator.model.castinfo;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class CastInfoMapper implements EntityMapper<CastInfo, CastInfoDTO> {

    @Override
    public CastInfoDTO toDTO(CastInfo castInfo) {
        return new CastInfoDTO(castInfo.getId(),
                castInfo.getMovie().getId(),
                castInfo.getPerson().getId(),
                castInfo.getRole().getId(),
                castInfo.getCharacterName());
    }

    public CastInfo toCastInfo(CastInfoDTO castInfoDTO,
                               Movie movie,
                               Person person,
                               Role role) {
        return new CastInfo(movie,
                person,
                role,
                castInfoDTO.getCharacterName());
    }
}
