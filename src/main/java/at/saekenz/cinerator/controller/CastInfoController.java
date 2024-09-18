package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.castinfo.*;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.service.ICastInfoService;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.service.IRoleService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/castinfo")
public class CastInfoController {

    @Autowired
    ICastInfoService castInfoService;

    @Autowired
    IMovieService movieService;

    @Autowired
    IPersonService personService;

    @Autowired
    IRoleService roleService;

    @Autowired
    CastInfoMapper castInfoMapper;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    ResponseBuilderService responseBuilderService;

    private final CastInfoDTOModelAssembler castInfoDTOModelAssembler;

    public CastInfoController(CastInfoDTOModelAssembler castInfoDTOModelAssembler) {
        this.castInfoDTOModelAssembler = castInfoDTOModelAssembler;
    }

    /**
     * Fetch every {@link CastInfo} from the database.
     *
     * @return ResponseEntity containing 200 Ok status and a collection of every
     * {@link CastInfo} stored in the database.
     */
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<CastInfoDTO>>> findAllCastInfos() {
        List<CastInfo> castInfos = castInfoService.findAll();

        if (castInfos.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<CastInfoDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(castInfos, castInfoMapper, castInfoDTOModelAssembler,
                        linkTo(methodOn(CastInfoController.class).findAllCastInfos()).withSelfRel());

        return ResponseEntity.ok(collectionModel);

    }

    /**
     * Fetch a specific {@link CastInfo} by its {@code id}.
     *
     * @param id the ID of the {@link CastInfo} that will be retrieved.
     * @return ResponseEntity containing 200 Ok status and the {@link CastInfo} resource.
     * (Returns 404 Not Found if the {@link CastInfo} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CastInfoDTO>> findCastInfoById(@PathVariable Long id) {
        CastInfo castInfo = castInfoService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, CastInfo.class.getSimpleName()));

        return ResponseEntity
                .ok(castInfoDTOModelAssembler
                        .toModel(castInfoMapper.toDTO(castInfo)));
    }

    /**
     * Creates a new {@link CastInfo}.
     *
     * @param castInfoCreationDTO a DTO containing data of the new {@link CastInfo}
     * @return ResponseEntity containing a 201 Created status and the created {@link CastInfo}.
     */
    @PostMapping
    public ResponseEntity<EntityModel<CastInfoDTO>> createCastInfo(
            @RequestBody CastInfoCreationDTO castInfoCreationDTO) {
        Movie movie = movieService.findById(castInfoCreationDTO.getMovieId()).orElseThrow(
                () -> new MovieNotFoundException(castInfoCreationDTO.getMovieId()));
        Person person = personService.findById(castInfoCreationDTO.getPersonId()).orElseThrow(
                () -> new ObjectNotFoundException(castInfoCreationDTO.getPersonId(), Person.class.getSimpleName()));
        Role role = roleService.findById(castInfoCreationDTO.getRoleId()).orElseThrow(
                () -> new ObjectNotFoundException(castInfoCreationDTO.getRoleId(), Role.class.getSimpleName()));

        CastInfo createdCastInfo = castInfoService.save(new CastInfo(movie, person, role,
                castInfoCreationDTO.getCharacterName()));

        EntityModel<CastInfoDTO> castInfoModel = castInfoDTOModelAssembler
                .toModel(castInfoMapper.toDTO(createdCastInfo));

        return responseBuilderService.buildCreatedResponseWithBody(castInfoModel);
    }

    /**
     * Updates a {@link CastInfo} based on its id.
     *
     * @param id the ID of the {@link CastInfo} to be updated
     * @param castInfoCreationDTO a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link CastInfo} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CastInfoDTO>> updateCastInfo(@PathVariable Long id,
                                                                   @RequestBody CastInfoCreationDTO castInfoCreationDTO) {
        CastInfo existingCastInfo = castInfoService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, CastInfo.class.getSimpleName()));

        if (!Objects.equals(existingCastInfo.getMovie().getId(), castInfoCreationDTO.getMovieId())) {
            Movie newMovie = movieService.findById(castInfoCreationDTO.getMovieId()).orElseThrow(
                    () -> new MovieNotFoundException(castInfoCreationDTO.getMovieId()));
            existingCastInfo.setMovie(newMovie);
        }

        if (!Objects.equals(existingCastInfo.getPerson().getId(), castInfoCreationDTO.getPersonId())) {
            Person newPerson = personService.findById(castInfoCreationDTO.getPersonId()).orElseThrow(
                    () -> new ObjectNotFoundException(castInfoCreationDTO.getPersonId(), Person.class.getSimpleName()));
            existingCastInfo.setPerson(newPerson);
        }

        if (!Objects.equals(existingCastInfo.getRole().getId(), castInfoCreationDTO.getRoleId())) {
            Role newRole = roleService.findById(castInfoCreationDTO.getRoleId()).orElseThrow(
                    () -> new ObjectNotFoundException(castInfoCreationDTO.getRoleId(), Role.class.getSimpleName()));
            existingCastInfo.setRole(newRole);
        }

        existingCastInfo.setCharacterName(castInfoCreationDTO.getCharacterName());

        EntityModel<CastInfoDTO> updatedCastInfo =  castInfoDTOModelAssembler
                .toModel(castInfoMapper.toDTO(castInfoService.save(existingCastInfo)));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedCastInfo);
    }

    /**
     * Deletes a {@link CastInfo} by its {@code id}.
     *
     * @param id the ID of the {@link CastInfo} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link CastInfo} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCastInfo(@PathVariable Long id) {
        castInfoService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, CastInfo.class.getSimpleName()));
        castInfoService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
