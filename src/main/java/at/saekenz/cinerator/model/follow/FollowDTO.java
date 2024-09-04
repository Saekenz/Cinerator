package at.saekenz.cinerator.model.follow;

public class FollowDTO {

    private Long userId;
    private Long followerId;

    public FollowDTO() {}

    public FollowDTO(Long userId, Long followerId) {
        this.userId = userId;
        this.followerId = followerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }
}

