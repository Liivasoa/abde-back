package mg.msys.abde_back.language.infrastructure.adapter.out.postgres;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import mg.msys.abde_back.language.application.port.in.query.dto.LanguageBookCountResult;
import mg.msys.abde_back.language.application.port.out.LanguagePersistencePort;
import mg.msys.abde_back.language.domain.Language;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.entity.LanguageEntity;
import mg.msys.abde_back.language.infrastructure.adapter.out.postgres.mapper.LanguageMapper;

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

    @Override
    public List<LanguageBookCountResult> findAvailableLanguagesWithBookCount() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
