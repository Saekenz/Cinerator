package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleDTO;
import at.saekenz.cinerator.model.role.RoleDTOModelAssembler;
import at.saekenz.cinerator.model.role.RoleMapper;
import at.saekenz.cinerator.service.IRoleService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    IRoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    private final RoleDTOModelAssembler roleDTOModelAssembler;

    private final PagedResourcesAssembler<RoleDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public RoleController(RoleDTOModelAssembler roleDTOModelAssembler) {
        this.roleDTOModelAssembler = roleDTOModelAssembler;
    }

    /**
     * Fetch every {@link Role} from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link Role} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link Role} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<RoleDTO>>> findAllRoles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<RoleDTO> roles = roleService.findAllPaged(page, size, sortField, sortDirection)
                .map(roleMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(roles, roleDTOModelAssembler));
    }

    /**
     * Fetch a specific {@link Role} by its {@code id}.
     *
     * @param id the ID of the {@link Role} that will be retrieved.
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link Role} resource.
     * (Returns 404 Not Found if the {@link Role} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RoleDTO>> findRoleById(@NotNull @PathVariable Long id) {
        Role role = roleService.findRoleById(id);

        return ResponseEntity.ok(roleDTOModelAssembler.toModel(roleMapper.toDTO(role)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Role}.
     *
     * @param roleDTO a DTO containing data of the new {@link Role}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link Role}.
     */
    @PostMapping
    public ResponseEntity<EntityModel<RoleDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        Role createdRole = roleService.createRole(roleDTO);
        EntityModel<RoleDTO> createdRoleModel = roleDTOModelAssembler.toModel(roleMapper.toDTO(createdRole));

        return responseBuilderService.buildCreatedResponseWithBody(createdRoleModel);
    }

    /**
     * Updates a {@link Role} based on its id.
     *
     * @param id the ID of the {@link Role} to be updated
     * @param roleDTO a DTO containing the needed data
     * @return {@link ResponseEntity<>} containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Role} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@NotNull @PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        Role existingRole = roleService.updateRole(id, roleDTO);
        EntityModel<RoleDTO> entityModel = roleDTOModelAssembler.toModel(
                roleMapper.toDTO(roleService.save(existingRole)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Deletes a {@link Role} by its {@code id}.
     *
     * @param id the ID of the {@link Role} to be deleted
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Role} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@NotNull @PathVariable Long id) {
        roleService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
