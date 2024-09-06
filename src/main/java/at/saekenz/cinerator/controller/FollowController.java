package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowDTO;
import at.saekenz.cinerator.model.follow.FollowKey;
import at.saekenz.cinerator.model.follow.FollowModelAssembler;
import at.saekenz.cinerator.service.IFollowService;
import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    IFollowService followService;

    private final FollowModelAssembler followAssembler;

    public FollowController(FollowModelAssembler followAssembler) {
        this.followAssembler = followAssembler;
    }

    @GetMapping("/{userId}/followers/{followerId}")
    public ResponseEntity<EntityModel<FollowDTO>> findByFollowKey(@PathVariable Long userId,
                                                               @PathVariable Long followerId) {
        Follow follow = followService.findByKey(new FollowKey(userId,followerId)).orElseThrow(
                () -> new PropertyValueException("Follow relation not found", "FollowKey","userId/followerId"));
        FollowDTO followDTO = new FollowDTO(follow.getId().getUserId(), follow.getId().getFollowerId());

        return ResponseEntity.ok(followAssembler.toModel(followDTO));
    }
}
