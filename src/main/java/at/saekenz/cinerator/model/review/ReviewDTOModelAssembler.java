package at.saekenz.cinerator.model.review;

import at.saekenz.cinerator.controller.MovieController;
import at.saekenz.cinerator.controller.ReviewController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReviewDTOModelAssembler implements RepresentationModelAssembler<ReviewDTO, EntityModel<ReviewDTO>> {

    @Override
    public EntityModel<ReviewDTO> toModel(ReviewDTO reviewDTO) {
        return EntityModel.of(reviewDTO,
                linkTo(methodOn(ReviewController.class).findDTOById(reviewDTO.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).findMovieByReviewId(reviewDTO.getId())).withRel("movie"),
                linkTo(methodOn(ReviewController.class).findUserByReviewId(reviewDTO.getId())).withRel("user"),
                linkTo(methodOn(MovieController.class).removeReviewById(reviewDTO.getMovieId(), reviewDTO.getId())).withRel("remove"));
    }
}
