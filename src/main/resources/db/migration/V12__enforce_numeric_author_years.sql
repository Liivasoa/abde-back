-- Keep only numeric years in [0..9999] now that BCE is not supported.
UPDATE author
SET birth_year = NULL
WHERE birth_year IS NOT NULL
  AND (birth_year < 0 OR birth_year > 9999);

UPDATE author
SET death_year = NULL
WHERE death_year IS NOT NULL
  AND (death_year < 0 OR death_year > 9999);

ALTER TABLE author
    ADD CONSTRAINT chk_author_birth_year_range
        CHECK (birth_year IS NULL OR birth_year BETWEEN 0 AND 9999),
    ADD CONSTRAINT chk_author_death_year_range
        CHECK (death_year IS NULL OR death_year BETWEEN 0 AND 9999);
