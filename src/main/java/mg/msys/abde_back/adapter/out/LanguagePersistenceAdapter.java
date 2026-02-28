package mg.msys.abde_back.adapter.out;

import java.util.Optional;

import org.springframework.stereotype.Component;

import mg.msys.abde_back.adapter.out.entity.LanguageEntity;
import mg.msys.abde_back.adapter.out.mapper.LanguageMapper;
import mg.msys.abde_back.adapter.out.repository.LanguageJpaRepository;
import mg.msys.abde_back.application.port.LanguagePersistencePort;
import mg.msys.abde_back.domain.model.Language;

@Component
public class LanguagePersistenceAdapter implements LanguagePersistencePort {

    private final LanguageJpaRepository languageJpaRepository;

    private final LanguageMapper languageMapper;

    public LanguagePersistenceAdapter(LanguageJpaRepository languageJpaRepository, LanguageMapper languageMapper) {
        this.languageJpaRepository = languageJpaRepository;
        this.languageMapper = languageMapper;
    }

    @Override
    public void save(Language language) {
        LanguageEntity entity = languageMapper.toEntity(language);
        languageJpaRepository.save(entity);
    }

    @Override
    public Optional<Language> findByCode(String code) {
        return languageJpaRepository.findById(code)
                .map(languageMapper::toDomain);
    }

}
