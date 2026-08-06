CREATE TABLE vacancy_applications (
    id UUID PRIMARY KEY,
    vacancy_id UUID NOT NULL REFERENCES vacancies(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    coordinator_id UUID NOT NULL REFERENCES coordinators(user_id),
    status VARCHAR(32) NOT NULL DEFAULT 'REFERRED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_vacancy_application_student UNIQUE (vacancy_id, student_id),
    CONSTRAINT ck_vacancy_application_status CHECK (
        status IN ('REFERRED', 'INTERVIEW_SCHEDULED', 'HIRED', 'REJECTED', 'WITHDRAWN'))
);

CREATE INDEX idx_vacancy_applications_student ON vacancy_applications (student_id);
CREATE INDEX idx_vacancy_applications_status ON vacancy_applications (status);

ALTER TABLE interviews
    DROP CONSTRAINT IF EXISTS interviews_student_id_key;

ALTER TABLE interviews
    ADD COLUMN application_id UUID REFERENCES vacancy_applications(id),
    ADD COLUMN notes TEXT,
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'SCHEDULED',
    ADD COLUMN outcome VARCHAR(32) NOT NULL DEFAULT 'PENDING';

ALTER TABLE interviews
    ADD CONSTRAINT uk_interviews_application UNIQUE (application_id),
    ADD CONSTRAINT ck_interview_status CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED')),
    ADD CONSTRAINT ck_interview_outcome CHECK (outcome IN ('PENDING', 'APPROVED', 'REJECTED'));

CREATE INDEX idx_interviews_student ON interviews (student_id);
