package mg.msys.abde_back.language.infrastructure.adapter.out.postgres;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.entity.LanguageEntity;

@Repository
public interface LanguageJpaRepository extends JpaRepository<LanguageEntity, String> {
    Optional<LanguageEntity> findByCode(String code);
}
