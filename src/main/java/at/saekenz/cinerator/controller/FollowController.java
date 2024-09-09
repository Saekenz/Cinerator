package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.*;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class FollowController {

    @Autowired
    IFollowService followService;

    @Autowired
    private FollowMapper followMapper;

    private final FollowDTOModelAssembler followAssembler;

    public FollowController(FollowDTOModelAssembler followAssembler) {
        this.followAssembler = followAssembler;
    }

    /**
     *
     * @return HTTP code 200 and a JSON representation of every existing {@link Follow} relationship
     */
    @GetMapping("/followers")
    public ResponseEntity<CollectionModel<EntityModel<FollowDTO>>> findAllFollows() {
        List<Follow> follows = followService.findAll();

        if (follows.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<FollowDTO>> collectionModel = CollectionModel.of(follows.stream()
                .map(followMapper::toDTO)
                .map(followAssembler::toModel)
                .toList(),
                linkTo(methodOn(FollowController.class).findAllFollows()).withSelfRel()
        );

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param userId the id of the {@link User} being followed
     * @param followerId the id of the {@link User} following
     * @return HTTP code 200 and a {@link FollowDTO} object if the relationship exists.
     * Otherwise, returns HTTP code 404 if the relationship is not found
     */
    @GetMapping("/users/{userId}/followers/{followerId}")
    public ResponseEntity<EntityModel<FollowDTO>> findByFollowKey(@PathVariable Long userId,
                                                               @PathVariable Long followerId) {
        FollowKey key = new FollowKey(userId, followerId);
        Follow follow = followService.findByKey(key).orElseThrow(
                () -> new FollowNotFoundException(key));
        FollowDTO followDTO = followMapper.toDTO(follow);

        return ResponseEntity.ok(followAssembler.toModel(followDTO));
    }
}
