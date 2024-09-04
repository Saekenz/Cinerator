package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowKey;
import at.saekenz.cinerator.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowServiceImpl implements IFollowService{

    @Autowired
    private FollowRepository followRepository;

    @Override
    public List<Follow> findAll() {
        return followRepository.findAll();
    }

    @Override
    public Optional<Follow> findByKey(FollowKey followKey) {
        return followRepository.findById(followKey);
    }

    @Override
    public Follow save(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public void deleteByKey(FollowKey followKey) {
        followRepository.deleteById(followKey);
    }
}
