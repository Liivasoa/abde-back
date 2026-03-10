CREATE TABLE language (
    code VARCHAR(3) PRIMARY KEY,
    label VARCHAR(255) NOT NULL
);

INSERT INTO language (code, label) VALUES
    ('EN', 'English'),
    ('FR', 'Français'),
    ('ES', 'Español'),
    ('DE', 'Deutsch'),
    ('IT', 'Italiano'),
    ('PT', 'Português'),
    ('MG', 'Malagasy');