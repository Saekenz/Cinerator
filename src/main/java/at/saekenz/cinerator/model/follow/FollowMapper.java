package at.saekenz.cinerator.model.follow;

import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class FollowMapper implements EntityMapper<Follow, FollowDTO> {

    public FollowDTO toDTO(Follow follow) {
        FollowDTO followDTO = new FollowDTO();

        followDTO.setUserId(follow.getId().getUserId());
        followDTO.setFollowerId(follow.getId().getFollowerId());
        followDTO.setFollowedAt(follow.getCreatedAt());

        return followDTO;
    }
}
