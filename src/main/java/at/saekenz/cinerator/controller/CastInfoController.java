package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.castinfo.*;
import at.saekenz.cinerator.service.ICastInfoService;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.service.IRoleService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private final PagedResourcesAssembler<CastInfoDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public CastInfoController(CastInfoDTOModelAssembler castInfoDTOModelAssembler) {
        this.castInfoDTOModelAssembler = castInfoDTOModelAssembler;
    }

    /**
     * Fetch every {@link CastInfo} from the database (in a paged format).
     *      *
     *      * @param page number of the page returned
     *      * @param size number of {@link CastInfo} resources returned for each page
     *      * @param sortField attribute that determines how returned resources will be sorted
     *      * @param sortDirection order of sorting (can be ASC or DESC)
     *      * @return {@link PagedModel} object with sorted/filtered {@link CastInfo} resources wrapped
     *      * in {@link ResponseEntity<>}
     *      */
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<CastInfoDTO>>> findAllCastInfos(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<CastInfoDTO> pagedCastInfos = castInfoService.findAllPaged(page, size, sortField, sortDirection)
                .map(castInfoMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(pagedCastInfos, castInfoDTOModelAssembler));
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
        CastInfo castInfo = castInfoService.findCastInfoById(id);

        return ResponseEntity.ok(castInfoDTOModelAssembler.toModel(castInfoMapper.toDTO(castInfo)));
    }

    /**
     * Creates a new {@link CastInfo}.
     *
     * @param castInfoCreationDTO a DTO containing data of the new {@link CastInfo}
     * @return ResponseEntity containing a 201 Created status and the created {@link CastInfo}.
     */
    @PostMapping
    public ResponseEntity<EntityModel<CastInfoDTO>> createCastInfo(
            @Valid @RequestBody CastInfoCreationDTO castInfoCreationDTO) {
        CastInfo createdCastInfo = castInfoService.createCastInfo(castInfoCreationDTO);
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
    public ResponseEntity<EntityModel<CastInfoDTO>> updateCastInfo(
            @NotNull @PathVariable Long id, @Valid @RequestBody CastInfoCreationDTO castInfoCreationDTO) {
        CastInfo updatedCastInfo = castInfoService.updateCastInfo(id, castInfoCreationDTO);
        EntityModel<CastInfoDTO> updatedCastInfoModel = castInfoDTOModelAssembler
                .toModel(castInfoMapper.toDTO(updatedCastInfo));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedCastInfoModel);
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
        castInfoService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
