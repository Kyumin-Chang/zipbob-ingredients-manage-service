package cloud.zipbob.ingredientsmanageservice.domain.refrigerator.repository;

import cloud.zipbob.ingredientsmanageservice.domain.refrigerator.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    Optional<Refrigerator> findByMemberId(Long memberId);
}
