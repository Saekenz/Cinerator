package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.repository.CastInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CastInfoServiceImpl implements ICastInfoService {

    @Autowired
    private CastInfoRepository castInfoRepository;

    @Override
    public List<CastInfo> findAll() {
        return castInfoRepository.findAll();
    }

    @Override
    public Optional<CastInfo> findById(Long id) {
        return castInfoRepository.findById(id);
    }

    @Override
    public CastInfo getReferenceById(Long id) {
        return castInfoRepository.getReferenceById(id);
    }

    @Override
    public CastInfo save(CastInfo castInfo) {
        return castInfoRepository.save(castInfo);
    }

    @Override
    public void deleteById(Long id) {
        castInfoRepository.deleteById(id);
    }
}
