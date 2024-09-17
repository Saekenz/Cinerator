package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastInfoRepository extends JpaRepository<CastInfo, Long> {
}
