package at.saekenz.cinerator.model.follow;

import org.springframework.stereotype.Component;

@Component
public class FollowMapper {

    public FollowDTO toDTO(Follow follow) {
        FollowDTO followDTO = new FollowDTO();

        followDTO.setUserId(follow.getId().getUserId());
        followDTO.setFollowerId(follow.getId().getFollowerId());
        followDTO.setFollowedAt(follow.getCreatedAt());

        return followDTO;
    }
}
