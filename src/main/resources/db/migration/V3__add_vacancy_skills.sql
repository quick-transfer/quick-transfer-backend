CREATE TABLE vacancy_skills (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(255) NOT NULL,
    skill_type VARCHAR(32) NOT NULL CHECK (skill_type IN ('TECHNICAL', 'SOCIOEMOTIONAL')),
    minimum_grade DOUBLE PRECISION NOT NULL
);

CREATE TABLE vacancy_skill_assignments (
    vacancy_id UUID NOT NULL REFERENCES vacancies(id) ON DELETE CASCADE,
    vacancy_skill_id UUID NOT NULL REFERENCES vacancy_skills(id) ON DELETE CASCADE,
    PRIMARY KEY (vacancy_id, vacancy_skill_id)
);

CREATE INDEX idx_vacancy_skill_assignments_skill
    ON vacancy_skill_assignments (vacancy_skill_id);
