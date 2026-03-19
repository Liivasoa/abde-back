ALTER TABLE book
ALTER COLUMN issued TYPE DATE
USING CASE
    WHEN issued IS NULL OR BTRIM(issued) = '' THEN NULL
    WHEN issued ~ '^\\d{4}$' THEN TO_DATE(issued || '-01-01', 'YYYY-MM-DD')
    WHEN issued ~ '^\\d{4}-\\d{2}-\\d{2}$' THEN issued::DATE
    ELSE NULL
END;
