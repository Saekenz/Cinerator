package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieDTO;
import at.saekenz.cinerator.model.movie.MovieDTOModelAssembler;
import at.saekenz.cinerator.model.movie.MovieMapper;
import at.saekenz.cinerator.model.review.*;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserDTO;
import at.saekenz.cinerator.model.user.UserDTOAssembler;
import at.saekenz.cinerator.model.user.UserMapper;
import at.saekenz.cinerator.service.IReviewService;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewMapper reviewMapper;
    private final ReviewDTOModelAssembler reviewDTOModelAssembler;

    private final UserMapper userMapper;
    private final UserDTOAssembler userDTOAssembler;

    private final MovieMapper movieMapper;
    private final MovieDTOModelAssembler movieDTOModelAssembler;

    @Autowired
    private IReviewService reviewService;

    private final PagedResourcesAssembler<ReviewDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public ReviewController(ReviewMapper reviewMapper, ReviewDTOModelAssembler reviewDTOModelAssembler,
                            UserMapper userMapper, UserDTOAssembler userDTOAssembler,
                            MovieMapper movieMapper, MovieDTOModelAssembler movieDTOModelAssembler) {
        this.reviewMapper = reviewMapper;
        this.reviewDTOModelAssembler = reviewDTOModelAssembler;
        this.userMapper = userMapper;
        this.userDTOAssembler = userDTOAssembler;
        this.movieMapper = movieMapper;
        this.movieDTOModelAssembler = movieDTOModelAssembler;
    }

    /**
     * Fetch every {@link Review} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link Review} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link Review} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ReviewDTO>>> findAllPaged(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<ReviewDTO> reviews = reviewService.findAllPaged(page, size, sortField, sortDirection)
                .map(reviewMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(reviews, reviewDTOModelAssembler));
    }

    /**
     * Fetch a specific {@link Review} by its {@code id}.
     *
     * @param id the ID of the {@link Review} that will be retrieved.
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link Review} resource.
     * (Returns 404 Not Found if the {@link Review} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReviewDTO>> findDTOById(@NotNull @Range(min = 1) @PathVariable Long id) {
        ReviewDTO reviewDTO = reviewService.findReviewDTOById(id);
        return ResponseEntity.ok(reviewDTOModelAssembler.toModel(reviewDTO));
    }

    /**
     * Fetches the {@link User} that created the {@link Review} specified by {@code id}.
     *
     * @param id id the ID of the {@link Review} for which the {@link User} is to be fetched
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link User} resource.
     * (Returns 404 Not Found if the {@link Review} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}/user")
    public ResponseEntity<EntityModel<UserDTO>> findUserByReviewId(@NotNull @Range(min = 1) @PathVariable Long id) {
        User user = reviewService.findUserByReviewId(id);
        EntityModel<UserDTO> userDTOEntityModel = userDTOAssembler.toModel(userMapper.toDTO(user));

        return ResponseEntity.ok(userDTOEntityModel);
    }

    /**
     * Fetches the {@link Movie} for which the {@link Review} specified by {@code id} was created.
     *
     * @param id id the ID of the {@link Review} for which the {@link Movie} is to be fetched
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link Movie} resource.
     * (Returns 404 Not Found if the {@link Review} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}/movie")
    public ResponseEntity<EntityModel<MovieDTO>> findMovieByReviewId(@NotNull @Range(min = 1) @PathVariable Long id) {
        Movie movie = reviewService.findMovieByReviewId(id);
        EntityModel<MovieDTO> movieDTOEntityModel = movieDTOModelAssembler.toModel(movieMapper.toDTO(movie));

        return ResponseEntity.ok(movieDTOEntityModel);
    }
}
