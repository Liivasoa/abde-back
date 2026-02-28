package mg.msys.abde_back.adapter.out.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;

@Repository
public interface LanguageJpaRepository extends JpaRepository<LanguageEntity, String> {

}
