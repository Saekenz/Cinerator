package at.saekenz.cinerator.model.role;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String role;

    @OneToMany(mappedBy = "role")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<CastInfo> castInfos;

    public Role() {}

    public Role(String role) {
        this.role = role;
        this.castInfos = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<CastInfo> getCastInfos() {
        return castInfos;
    }
}
