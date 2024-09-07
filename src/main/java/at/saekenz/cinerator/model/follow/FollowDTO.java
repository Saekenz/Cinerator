package at.saekenz.cinerator.model.follow;

import java.time.LocalDateTime;

public class FollowDTO {

    private Long userId;
    private Long followerId;
    private LocalDateTime followedAt;

    public FollowDTO() {}

    public FollowDTO(Long userId, Long followerId, LocalDateTime followedAt) {
        this.userId = userId;
        this.followerId = followerId;
        this.followedAt = followedAt;
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

    public LocalDateTime getFollowedAt() { return followedAt; }

    public void setFollowedAt(LocalDateTime followedAt) { this.followedAt = followedAt; }
}

