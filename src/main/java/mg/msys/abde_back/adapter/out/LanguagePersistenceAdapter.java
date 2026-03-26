package mg.msys.abde_back.adapter.out;

import java.util.Optional;

import org.springframework.stereotype.Component;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;
import mg.msys.abde_back.adapter.out.mapper.LanguageMapper;
import mg.msys.abde_back.domain.model.Language;
import mg.msys.abde_back.infrastructure.repository.LanguageJpaRepository;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePort;

@Component
public class LanguagePersistenceAdapter implements LanguagePersistencePort {

    private final LanguageJpaRepository languageJpaRepository;

    private final LanguageMapper languageMapper;

    public LanguagePersistenceAdapter(LanguageJpaRepository languageJpaRepository, LanguageMapper languageMapper) {
        this.languageJpaRepository = languageJpaRepository;
        this.languageMapper = languageMapper;
    }

    @Override
    public Language save(Language language) {
        LanguageEntity entity = languageMapper.toEntity(language);
        LanguageEntity savedEntity = languageJpaRepository.save(entity);
        return languageMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Language> findByCode(String code) {
        return languageJpaRepository.findByCode(code)
                .map(languageMapper::toDomain);
    }

}
