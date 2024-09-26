package at.saekenz.cinerator.model.follow;

import org.hibernate.validator.constraints.Range;

public record FollowActionDTO(@Range(min = 1) Long followerId) {

}
