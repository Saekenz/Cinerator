package at.saekenz.cinerator.model.userlist;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "userlists")
public class UserList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private boolean isPrivate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USR_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "userlist_movie",
            joinColumns = @JoinColumn(name = "USRLIST_ID"),
            inverseJoinColumns = @JoinColumn(name = "MOV_ID"))
    private List<Movie> movielist;

    public UserList() {}

    public UserList(String name, String description, boolean isPrivate,
                    User user, List<Movie> movielist) {
        this.name = name;
        this.description = description;
        this.isPrivate = isPrivate;
        this.user = user;
        this.movielist = movielist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Movie> getMovielist() {
        return movielist;
    }

    public void setMovielist(List<Movie> movielist) {
        this.movielist = movielist;
    }
}
