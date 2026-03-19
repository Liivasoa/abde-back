ALTER TABLE author
    ALTER COLUMN birth_year TYPE INTEGER
        USING CASE
            WHEN birth_year IS NULL OR BTRIM(birth_year) = '' THEN NULL
            WHEN birth_year ~* 'BCE' THEN -(REGEXP_REPLACE(birth_year, '[^0-9]', '', 'g')::INTEGER)
            ELSE REGEXP_REPLACE(birth_year, '[^0-9]', '', 'g')::INTEGER
        END,
    ALTER COLUMN death_year TYPE INTEGER
        USING CASE
            WHEN death_year IS NULL OR BTRIM(death_year) = '' THEN NULL
            WHEN death_year ~* 'BCE' THEN -(REGEXP_REPLACE(death_year, '[^0-9]', '', 'g')::INTEGER)
            ELSE REGEXP_REPLACE(death_year, '[^0-9]', '', 'g')::INTEGER
        END;
