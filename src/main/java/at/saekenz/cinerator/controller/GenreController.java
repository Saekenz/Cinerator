package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTO;
import at.saekenz.cinerator.model.genre.GenreDTOModelAssembler;
import at.saekenz.cinerator.model.genre.GenreMapper;
import at.saekenz.cinerator.service.IGenreService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    IGenreService genreService;

    @Autowired
    GenreMapper genreMapper;

    @Autowired
    ResponseBuilderService responseBuilderService;

    private final GenreDTOModelAssembler genreDTOModelAssembler;

    private final PagedResourcesAssembler<GenreDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public GenreController(GenreDTOModelAssembler genreDTOModelAssembler) {
        this.genreDTOModelAssembler = genreDTOModelAssembler;
    }

    /**
     * Fetch every {@link Genre} from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link Genre} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link Genre} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<GenreDTO>>> findAllGenres(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<GenreDTO> genres = genreService.findAllPaged(page, size, sortField, sortDirection)
                .map(genreMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(genres, genreDTOModelAssembler));
    }

    /**
     * Fetch a specific {@link Genre} by its {@code id}.
     *
     * @param id the ID of the {@link Genre} that will be retrieved.
     * @return ResponseEntity containing 200 Ok status and the {@link Genre} resource.
     * (Returns 404 Not Found if the {@link Genre} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findGenreById(@PathVariable Long id) {
        Genre genre = genreService.findGenreById(id);

        return ResponseEntity.ok(genreDTOModelAssembler.toModel(genreMapper.toDTO(genre)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Genre}.
     *
     * @param genreDTO a DTO containing data of the new {@link Genre}
     * @return ResponseEntity containing a 201 Created status and the created {@link Genre}.
     */
    @PostMapping()
    public ResponseEntity<EntityModel<GenreDTO>> createGenre(@Valid @RequestBody GenreDTO genreDTO) {
        Genre createdGenre = genreService.createGenre(genreDTO);
        EntityModel<GenreDTO> createdGenreModel = genreDTOModelAssembler.toModel(genreMapper
                .toDTO(createdGenre));

        return responseBuilderService.buildCreatedResponseWithBody(createdGenreModel);
    }

    /**
     * Updates a {@link Genre} based on its id.
     *
     * @param id the ID of the {@link Genre} to be updated
     * @param genreDTO a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Genre} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDTO genreDTO) {
        Genre updatedGenre = genreService.updateGenre(id, genreDTO);
        EntityModel<GenreDTO> updatedGenreModel = genreDTOModelAssembler.toModel(genreMapper
                .toDTO(updatedGenre));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedGenreModel);
    }

    /**
     * Deletes a {@link Genre} by its {@code id}.
     *
     * @param id the ID of the {@link Genre} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Genre} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGenre(@PathVariable Long id) {
        genreService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
