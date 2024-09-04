package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowKey;

import java.util.List;
import java.util.Optional;

public interface IFollowService {

    List<Follow> findAll();

    Optional<Follow> findByKey(FollowKey followKey);

    Follow save(Follow follow);

    void deleteByKey(FollowKey followKey);
}
