package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.*;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.service.IFollowService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FollowController {

    @Autowired
    IFollowService followService;

    private final FollowDTOModelAssembler followAssembler;
    private final FollowMapper followMapper;

    private final PagedResourcesAssembler<FollowDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public FollowController(FollowDTOModelAssembler followAssembler,
                            FollowMapper followMapper) {
        this.followAssembler = followAssembler;
        this.followMapper = followMapper;
    }

    /**
     * Fetches all {@link Follow} resources from the database(in a paged format).
     *      *
     *      * @param page number of the page returned
     *      * @param size number of {@link Follow} resources returned for each page
     *      * @param sortField attribute that determines how returned resources will be sorted
     *      * @param sortDirection order of sorting (can be ASC or DESC)
     *      * @return {@link PagedModel} object with sorted/filtered {@link Follow} resources wrapped
     *      * in {@link ResponseEntity<>}
     *      */
    @GetMapping("/followers")
    public ResponseEntity<PagedModel<EntityModel<FollowDTO>>> findAllFollows(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<FollowDTO> follows = followService.findAllPaged(page, size, sortField, sortDirection)
                .map(followMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(follows, followAssembler));
    }

    /**
     *
     * @param userId the id of the {@link User} being followed
     * @param followerId the id of the {@link User} following
     * @return HTTP code 200 and a {@link FollowDTO} object if the relationship exists.
     * Otherwise, returns HTTP code 404 if the relationship is not found
     */
    @GetMapping("/users/{userId}/followers/{followerId}")
    public ResponseEntity<EntityModel<FollowDTO>> findByFollowKey(@NotNull @PathVariable Long userId,
                                                               @NotNull @PathVariable Long followerId) {
        Follow follow = followService.findFollowByKey(new FollowKey(userId, followerId));

        return ResponseEntity.ok(followAssembler.toModel(followMapper.toDTO(follow)));
    }
}
