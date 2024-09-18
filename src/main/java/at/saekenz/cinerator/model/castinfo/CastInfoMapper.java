package at.saekenz.cinerator.model.castinfo;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieMapper;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleMapper;
import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class CastInfoMapper implements EntityMapper<CastInfo, CastInfoDTO> {

    private final MovieMapper movieMapper;
    private final PersonMapper personMapper;
    private final RoleMapper roleMapper;

    public CastInfoMapper(MovieMapper movieMapper, PersonMapper personMapper, RoleMapper roleMapper) {
        this.movieMapper = movieMapper;
        this.personMapper = personMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public CastInfoDTO toDTO(CastInfo castInfo) {
        return new CastInfoDTO(castInfo.getId(),
                movieMapper.toDTO(castInfo.getMovie()),
                personMapper.toDTO(castInfo.getPerson()),
                roleMapper.toDTO(castInfo.getRole()),
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
