package at.saekenz.cinerator.model.follow;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FollowKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "follower_id")
    Long followerId;

    public FollowKey() {}

    public FollowKey(Long userId, Long followerId) {
        this.userId = userId;
        this.followerId = followerId;
    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFollowerId() { return followerId; }

    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FollowKey followKey)) return false;
        return Objects.equals(userId, followKey.userId) && Objects.equals(followerId, followKey.followerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, followerId);
    }
}
