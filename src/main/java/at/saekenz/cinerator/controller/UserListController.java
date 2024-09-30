package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.*;
import at.saekenz.cinerator.model.user.*;
import at.saekenz.cinerator.model.userlist.*;
import at.saekenz.cinerator.service.IUserListService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/lists")
public class UserListController {

    @Autowired
    IUserListService userListService;

    @Autowired
    UserListMapper userListMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ResponseBuilderService responseBuilderService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    private final UserListDTOModelAssembler userListDTOAssembler;
    private final UserDTOAssembler userDTOAssembler;

    private final PagedResourcesAssembler<UserListDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public UserListController(UserListDTOModelAssembler userListDTOAssembler,
                              UserDTOAssembler userDTOAssembler) {
        this.userListDTOAssembler = userListDTOAssembler;
        this.userDTOAssembler = userDTOAssembler;
    }

    /**
     * Fetch every {@link UserList} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link UserList} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link UserList} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserListDTO>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<UserListDTO> userLists = userListService.findAllPaged(page, size, sortField, sortDirection)
                .map(userListMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(userLists, userListDTOAssembler));
    }

    /**
     * Fetch a specific {@link UserList} by its {@code id}.
     *
     * @param id the ID of the {@link UserList} that will be retrieved.
     * @return {@link ResponseEntity} containing a 200 Ok status and the {@link UserList}.
     * (Returns 404 Not Found if the {@link UserList} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserListDTO>> findById(@NotNull @Range(min = 1) @PathVariable Long id) {
        UserList userList = userListService.findUserListById(id);

        return ResponseEntity.ok(userListDTOAssembler.toModel(userListMapper.toDTO(userList)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link UserList}.
     *
     * @param userListCreationDTO a DTO containing data of the new {@link UserList}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link UserList}
     * (or a 404 Not Found if the {@link User} owning the new {@link UserList} does not exist in the database).
     */
    @PostMapping
    public ResponseEntity<EntityModel<UserListDTO>> createUserList(@Valid @RequestBody UserListCreationDTO userListCreationDTO) {
        UserList createdUserList = userListService.createUserList(userListCreationDTO);
        EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper.toDTO(createdUserList));

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
    }

    /**
     * Updates a {@link UserList} based on their {@code id}.
     *
     * @param id the ID of the {@link UserList} to be updated
     * @param userListDTO a DTO containing the needed data
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link UserList} exists with the specified {@code id}.)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUserList(@NotNull @Range(min = 1) @PathVariable Long id,
                                            @Valid @RequestBody UserListDTO userListDTO) {
        UserList updatedUserList = userListService.updateUserList(id, userListDTO);
        EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper.toDTO(updatedUserList));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Deletes a {@link UserList} by their {@code id}.
     *
     * @param id the ID of the {@link UserList} to be deleted
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link UserList} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserList(@NotNull @Range(min = 1) @PathVariable Long id) {
        userListService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ------------------------------------ SEARCH ---------------------------------------------------------------------

    /**
     *
     * @param name name of the searched for list(s)
     * @param description description of the searched for list(s)
     * @param userId id of the {@link User} that owns the {@link UserList}
     * @return {@link CollectionModel} object containing lists matching search parameters
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<UserListDTO>>> searchUserLists(
            @RequestParam(required = false) String name, @RequestParam(required = false) String description,
            @RequestParam(required = false) Long userId) {
        List<UserList> foundLists = userListService.searchUserLists(name, description, userId);

        if (foundLists.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       CollectionModel<EntityModel<UserListDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(foundLists, userListMapper, userListDTOAssembler,
                       linkTo(methodOn(UserListController.class).searchUserLists(name,description,userId))
                               .withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ---------------------------------- OTHER -------------------------------------------------------------------------

    /**
     * Fetches the {@link User} that owns the {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} for which the {@link User} is to be retrieved.
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the {@link User} (or a
     * 404 Not Found status if no {@link UserList} exists with the specified {@code id}).
     */
    @GetMapping("/{id}/user")
    public ResponseEntity<EntityModel<UserDTO>> findUserByUserList(@NotNull @Range(min = 1) @PathVariable Long id) {
        User user = userListService.findUserByUserListId(id);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(user));

        return ResponseEntity.ok(entityModel);
    }

    /**
     * Fetches movies from a {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} for which the movies are to be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the movies associated with this
     * {@link UserList} (or a 404 Not Found status if the {@link UserList} does not exist).
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesByUserList(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<Movie> foundMovies = userListService.findMoviesByUserListId(id);

        if (foundMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundMovies,
                linkTo(methodOn(UserListController.class).findMoviesByUserList(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Adds a {@link Movie} to the {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} to which the {@link Movie} is to be added
     * @param movieId the ID of the {@link Movie} to be added
     * @return {@link ResponseEntity<>} containing a 204 No Content status and a link to the updated {@link UserList}
     * resource. It returns a 404 Not Found status if the {@link UserList} does not exist or a 400 Bad Request status
     * if the {@link Movie} is already present in this {@link UserList}.
     */
    @PutMapping("/{id}/movies")
    public ResponseEntity<Object> addMovieToUserList(@NotNull @Range(min = 1) @PathVariable Long id,
                                                @NotNull @RequestBody Long movieId) {
        UserList updatedUserList = userListService.addMovieToUserListById(id, movieId);
        EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper.toDTO(updatedUserList));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     *  Removes a {@link Movie} resource specified by {@code movieId} from the {@link UserList}
     *  specified by by {@code userId}
     *
     * @param id the ID of the {@link UserList} from which the {@link Movie} is to be removed
     * @param movieId the ID of the {@link Movie} to be removed
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a 404 Not Found status if the
     *      * {@link UserList} does not exist or the {@link Movie} is not found in the {@link UserList}).
     */
    @DeleteMapping("{id}/movies/{movieId}")
    public ResponseEntity<?> removeMovieFromUserList(@NotNull @Range(min = 1) @PathVariable Long id,
                                                     @NotNull @Range(min = 1) @PathVariable Long movieId) {
        userListService.removeMovieFromUserListId(id, movieId);

        return ResponseEntity.noContent().build();
    }
}
