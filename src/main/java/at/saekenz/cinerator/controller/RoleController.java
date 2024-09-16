package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleDTO;
import at.saekenz.cinerator.model.role.RoleDTOModelAssembler;
import at.saekenz.cinerator.model.role.RoleMapper;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    IRoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    private CollectionModelBuilderService modelBuilderService;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    private final RoleDTOModelAssembler roleDTOModelAssembler;
//    private final Logger log = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RoleDTOModelAssembler roleDTOModelAssembler) {
        this.roleDTOModelAssembler = roleDTOModelAssembler;
    }

    /**
     * Fetch every {@link Role} from the database.
     *
     * @return ResponseEntity containing 200 Ok status and a collection of every
     * {@link Role} stored in the database.
     */
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<RoleDTO>>> findAllRoles() {
        List<Role> roles = roleService.findAll();

        if (roles.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<RoleDTO>> collectionModel = modelBuilderService
                .createCollectionModelFromList(roles, roleMapper, roleDTOModelAssembler,
                        linkTo(methodOn(RoleController.class).findAllRoles()).withSelfRel());

        return ResponseEntity.ok(collectionModel);

    }

    /**
     * Fetch a specific {@link Role} by its {@code id}.
     *
     * @param id the ID of the {@link Role} that will be retrieved.
     * @return ResponseEntity containing 200 Ok status and the {@link Role} resource.
     * (Returns 404 Not Found if the {@link Role} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RoleDTO>> findRoleById(@PathVariable Long id) {
        Role role = roleService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Role.class.getSimpleName()));

        return ResponseEntity
                .ok(roleDTOModelAssembler
                        .toModel(roleMapper.toDTO(role)));
    }

    /**
     * Creates a new {@link Role}.
     *
     * @param roleDTO a DTO containing data of the new {@link Role}
     * @return ResponseEntity containing a 201 Created status and the created {@link Role}.
     */
    @PostMapping
    public ResponseEntity<EntityModel<RoleDTO>> createRole(@RequestBody RoleDTO roleDTO) {
        Role createdRole = roleService.save(roleMapper.toRole(roleDTO));
        EntityModel<RoleDTO> roleModel = roleDTOModelAssembler
                .toModel(roleMapper.toDTO(createdRole));

        return responseBuilderService.buildCreatedResponseWithBody(roleModel);
    }

    /**
     * Updates a {@link Role} based on its id.
     *
     * @param id the ID of the {@link Role} to be updated
     * @param roleDTO a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Role} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        Role existingRole = roleService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Role.class.getSimpleName()));

        existingRole.setRole(roleDTO.role());
        EntityModel<RoleDTO> entityModel = roleDTOModelAssembler
                .toModel(roleMapper.toDTO(roleService.save(existingRole)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Deletes a {@link Role} by its {@code id}.
     *
     * @param id the ID of the {@link Role} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Role} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        Role existingRole = roleService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Role.class.getSimpleName()));

        roleService.delete(existingRole);

        return ResponseEntity.noContent().build();
    }
}
