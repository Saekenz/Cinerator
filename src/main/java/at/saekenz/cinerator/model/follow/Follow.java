package at.saekenz.cinerator.model.follow;

import at.saekenz.cinerator.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "followers")
public class Follow {

    @EmbeddedId
    private FollowKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name = "follower_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User follower;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Follow() {}

    public Follow(FollowKey id, User user, User follower) {
        this.id = id;
        this.user = user;
        this.follower = follower;
    }

    public FollowKey getId() { return id; }

    public void setId(FollowKey id) { this.id = id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public User getFollower() { return follower; }

    public void setFollower(User follower) { this.follower = follower; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", user=" + user +
                ", follower=" + follower +
                ", createdAt=" + createdAt +
                '}';
    }
}
