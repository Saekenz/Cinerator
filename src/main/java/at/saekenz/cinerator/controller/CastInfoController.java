package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.castinfo.CastInfoDTO;
import at.saekenz.cinerator.model.castinfo.CastInfoDTOModelAssembler;
import at.saekenz.cinerator.model.castinfo.CastInfoMapper;
import at.saekenz.cinerator.service.ICastInfoService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/castinfo")
public class CastInfoController {

    @Autowired
    ICastInfoService castInfoService;

    @Autowired
    CastInfoMapper castInfoMapper;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

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

}
