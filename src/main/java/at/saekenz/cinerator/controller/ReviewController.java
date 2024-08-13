package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewModelAssembler;
import at.saekenz.cinerator.model.review.ReviewNotFoundException;
import at.saekenz.cinerator.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    IReviewService reviewService;

    private final ReviewModelAssembler assembler;

    public ReviewController(ReviewModelAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Review>> findAll() {
        List<EntityModel<Review>> reviews = reviewService.findAll().stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(reviews, linkTo(methodOn(ReviewController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Review> findById(@PathVariable Long id) {
        Review review = reviewService.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        return assembler.toModel(review);
    }

    @PostMapping()
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        EntityModel<Review> entityModel = assembler.toModel(reviewService.save(review));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Review updatedReview = reviewService.findById(id).map(
                review -> {
                    review.setComment(newReview.getComment());
                    review.setRating(newReview.getRating());
                    review.setIs_liked(newReview.isIs_liked());
                    return reviewService.save(review);
                })
                .orElseGet(() -> reviewService.save(newReview));
        EntityModel<Review> entityModel = assembler.toModel(updatedReview);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/user/{user_id}")
    public CollectionModel<EntityModel<Review>> findByUserId(@PathVariable Long user_id) {
        List<EntityModel<Review>> reviews = reviewService.findReviewsByUser(user_id).stream()
                .map(assembler::toModel).toList();
        return CollectionModel.of(reviews, linkTo(methodOn(ReviewController.class).findByUserId(user_id)).withSelfRel());
    }

    @GetMapping("/user/{user_id}/likes")
    public CollectionModel<EntityModel<Review>> findLikedByUser(@PathVariable Long user_id) {
        List<EntityModel<Review>> reviews = reviewService.findReviewsLikedByUser(user_id).stream()
                .map(assembler::toModel).toList();
        return CollectionModel.of(reviews, linkTo(methodOn(ReviewController.class).findLikedByUser(user_id)).withSelfRel());
    }

    @GetMapping("user/{user_id}/rating/{rating}")
    public CollectionModel<EntityModel<Review>> findRatingByUser(@PathVariable Long user_id, @PathVariable Integer rating) {
        List<EntityModel<Review>> reviews = reviewService.findReviewsRatedByUser(user_id, rating).stream()
                .map(assembler::toModel).toList();
        return CollectionModel.of(reviews, linkTo(methodOn(ReviewController.class).findRatingByUser(user_id, rating)).withSelfRel());
    }
}
