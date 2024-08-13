package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.controller.MovieController;
import at.saekenz.cinerator.controller.ReviewController;
import at.saekenz.cinerator.controller.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReviewModelAssembler implements RepresentationModelAssembler<Review, EntityModel<Review>> {

    @Override
    public EntityModel<Review> toModel(Review review) {
        return EntityModel.of(review,
                linkTo(methodOn(ReviewController.class).findById(review.getReview_id())).withSelfRel(),
                linkTo(methodOn(UserController.class).findById(review.getUser().getUser_id())).withRel("author"),
                linkTo(methodOn(MovieController.class).findById(review.getMovie().getMovie_id())).withRel("movie"),
                linkTo(methodOn(ReviewController.class).findAll()).withRel("reviews"));
    }
}
