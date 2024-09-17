package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;

import java.util.List;
import java.util.Optional;

public interface ICastInfoService {

    List<CastInfo> findAll();

    Optional<CastInfo> findById(Long id);

    CastInfo getReferenceById(Long id);

    CastInfo save(CastInfo castInfo);

    void deleteById(Long id);
}
