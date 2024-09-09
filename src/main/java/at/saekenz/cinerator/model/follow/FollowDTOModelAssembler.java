package at.saekenz.cinerator.model.follow;

import at.saekenz.cinerator.controller.FollowController;
import at.saekenz.cinerator.controller.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FollowDTOModelAssembler implements RepresentationModelAssembler<FollowDTO, EntityModel<FollowDTO>> {

    @Override
    public EntityModel<FollowDTO> toModel(FollowDTO follow) {
        Long userId = follow.getUserId();
        Long followerId = follow.getFollowerId();

        return EntityModel.of(follow,
                linkTo(methodOn(FollowController.class).findByFollowKey(userId, followerId)).withSelfRel(),
                linkTo(methodOn(UserController.class).findById(userId)).withRel("user"),
                linkTo(methodOn(UserController.class).findById(followerId)).withRel("follower"));

    }
}
