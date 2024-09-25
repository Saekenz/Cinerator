package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowKey;
import at.saekenz.cinerator.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public Follow findFollowByKey(FollowKey followKey) {
        return findByKey(followKey).orElseThrow(
                () ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("User with id %s is currently not following user with id %s!",
                                followKey.getFollowerId(), followKey.getUserId())));
    }

    @Override
    public Follow save(Follow follow) {
        return followRepository.save(follow);
    }

    @Override
    public void deleteByKey(FollowKey followKey) {
        followRepository.deleteById(followKey);
    }

    @Override
    public Page<Follow> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return followRepository.findAll(pageable);
    }
}
