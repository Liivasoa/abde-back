package mg.msys.abde_back.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;

@Repository
public interface LanguageJpaRepository extends JpaRepository<LanguageEntity, Long> {

    /**
     * Find a language by its code.
     *
     * @param code the language code
     * @return Optional containing the language if found
     */
    Optional<LanguageEntity> findByCode(String code);
}
