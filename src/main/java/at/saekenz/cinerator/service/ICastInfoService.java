package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.castinfo.CastInfoCreationDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ICastInfoService {

    List<CastInfo> findAll();

    Optional<CastInfo> findById(Long id);

    CastInfo findCastInfoById(Long id);

    CastInfo getReferenceById(Long id);

    CastInfo save(CastInfo castInfo);

    void deleteById(Long id);

    Page<CastInfo> findAllPaged(int page, int size, String sortField, String sortDirection);

    CastInfo createCastInfo(CastInfoCreationDTO castInfoCreationDTO);

    CastInfo updateCastInfo(Long id, CastInfoCreationDTO castInfoCreationDTO);
}
