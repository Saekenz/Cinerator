package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewModelAssembler;
import at.saekenz.cinerator.model.review.ReviewNotFoundException;
import at.saekenz.cinerator.service.IReviewService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    IReviewService reviewService;

    @Autowired
    ResponseBuilderService responseBuilderService;

    private final ReviewModelAssembler assembler;

    public ReviewController(ReviewModelAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findAll() {
        List<Review> reviews = reviewService.findAll();

        if (reviews.isEmpty()) {
            throw new ReviewNotFoundException();
        }

        List<EntityModel<Review>> reviewModels = reviews.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
                linkTo(methodOn(ReviewController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Review>> findById(@PathVariable Long id) {
        Review review = reviewService.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        return ResponseEntity.ok(assembler.toModel(review));
    }

    @PostMapping()
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        EntityModel<Review> entityModel = assembler.toModel(reviewService.save(review));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.findById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        reviewService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody Review newReview) {
        Optional<Review> existingReview = reviewService.findById(id);

        Review updatedReview = existingReview.map(
                review -> {
                    review.setComment(newReview.getComment());
                    review.setRating(newReview.getRating());
                    review.setIsLiked(newReview.isLiked());
                    return reviewService.save(review);
                })
                .orElseGet(() -> reviewService.save(newReview));
        EntityModel<Review> entityModel = assembler.toModel(updatedReview);

        if (existingReview.isPresent()) {
            return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
        }
        else {
            return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findByUserId(@PathVariable Long user_id) {
        List<Review> reviews = reviewService.findReviewsByUser(user_id);

        if (reviews.isEmpty()) { throw new ReviewNotFoundException(); }

        List<EntityModel<Review>> reviewModels = reviews.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
                linkTo(methodOn(ReviewController.class).findByUserId(user_id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/user/{user_id}/likes")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findLikedByUser(@PathVariable Long user_id) {
        List<Review> reviews = reviewService.findReviewsLikedByUser(user_id);

        if (reviews.isEmpty()) { throw new ReviewNotFoundException(); }

        List<EntityModel<Review>> reviewModels = reviews.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
                linkTo(methodOn(ReviewController.class).findLikedByUser(user_id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("user/{user_id}/rating/{rating}")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findRatingByUser(@PathVariable Long user_id, @PathVariable Integer rating) {
        List<Review> reviews = reviewService.findReviewsRatedByUser(user_id, rating);

        if (reviews.isEmpty()) { throw new ReviewNotFoundException(); }

        List<EntityModel<Review>> reviewModels = reviews.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
                linkTo(methodOn(ReviewController.class).findRatingByUser(user_id, rating)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
