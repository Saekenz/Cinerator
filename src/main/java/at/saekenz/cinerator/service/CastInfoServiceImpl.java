package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.castinfo.CastInfoCreationDTO;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.repository.CastInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CastInfoServiceImpl implements ICastInfoService {
    private final IMovieService movieService;
    private final IPersonService personService;
    private final IRoleService roleService;

    @Autowired
    private CastInfoRepository castInfoRepository;

    public CastInfoServiceImpl(IMovieService movieService,
                               IPersonService personService,
                               IRoleService roleService) {
        this.movieService = movieService;
        this.personService = personService;
        this.roleService = roleService;
    }

    @Override
    public List<CastInfo> findAll() {
        return castInfoRepository.findAll();
    }

    @Override
    public Optional<CastInfo> findById(Long id) {
        return castInfoRepository.findById(id);
    }

    @Override
    public CastInfo findCastInfoById(Long id) {
        return findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("CastInfo with id %s could not be found!", id)));
    }

    @Override
    public CastInfo getReferenceById(Long id) {
        return castInfoRepository.getReferenceById(id);
    }

    @Override
    public CastInfo save(CastInfo castInfo) {
        return castInfoRepository.save(castInfo);
    }

    @Override
    public void deleteById(Long id) {
        findCastInfoById(id);
        castInfoRepository.deleteById(id);
    }

    @Override
    public Page<CastInfo> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return castInfoRepository.findAll(pageable);
    }

    @Override
    public CastInfo createCastInfo(CastInfoCreationDTO castInfoCreationDTO) {
        Movie movie = movieService.getReferenceById(castInfoCreationDTO.getMovieId());
        Person person = personService.getReferenceById(castInfoCreationDTO.getPersonId());
        Role role = roleService.getReferenceById(castInfoCreationDTO.getRoleId());

        return save(new CastInfo(movie, person, role,
                castInfoCreationDTO.getCharacterName()));
    }

    @Override
    public CastInfo updateCastInfo(Long id, CastInfoCreationDTO castInfoCreationDTO) {
        CastInfo existingCastInfo = findCastInfoById(id);

        if (!Objects.equals(castInfoCreationDTO.getMovieId(), existingCastInfo.getMovie().getId())) {
            Movie updatedMovie = movieService.getReferenceById(castInfoCreationDTO.getMovieId());
            existingCastInfo.setMovie(updatedMovie);
        }

        if (!Objects.equals(castInfoCreationDTO.getPersonId(), existingCastInfo.getPerson().getId())) {
            Person updatedPerson = personService.getReferenceById(castInfoCreationDTO.getPersonId());
            existingCastInfo.setPerson(updatedPerson);
        }

        if (!Objects.equals(castInfoCreationDTO.getRoleId(), existingCastInfo.getRole().getId())) {
            Role updatedRole = roleService.getReferenceById(castInfoCreationDTO.getRoleId());
            existingCastInfo.setRole(updatedRole);
        }

        existingCastInfo.setCharacterName(castInfoCreationDTO.getCharacterName());

        return save(existingCastInfo);
    }
}
