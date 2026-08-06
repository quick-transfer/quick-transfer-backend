ALTER TABLE users
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE places
    ADD COLUMN code VARCHAR(50),
    ADD COLUMN description TEXT,
    ADD COLUMN city VARCHAR(100),
    ADD COLUMN state VARCHAR(2),
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';

UPDATE places
SET code = 'PLC-' || UPPER(SUBSTRING(CAST(id AS VARCHAR) FROM 1 FOR 8))
WHERE code IS NULL;

ALTER TABLE places
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE places
    ADD CONSTRAINT uk_places_code UNIQUE (code),
    ADD CONSTRAINT ck_places_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED'));

ALTER TABLE courses
    ADD COLUMN code VARCHAR(50),
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE';

UPDATE courses
SET code = 'CRS-' || UPPER(SUBSTRING(CAST(id AS VARCHAR) FROM 1 FOR 8))
WHERE code IS NULL;

ALTER TABLE courses
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE courses
    ADD CONSTRAINT uk_courses_code UNIQUE (code),
    ADD CONSTRAINT ck_courses_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED'));

ALTER TABLE class_entities
    ADD COLUMN name VARCHAR(100),
    ADD COLUMN max_students BIGINT NOT NULL DEFAULT 30;

UPDATE class_entities
SET name = acronym
WHERE name IS NULL;

ALTER TABLE class_entities
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE class_entities
    ADD CONSTRAINT ck_class_max_students CHECK (max_students > 0);

ALTER TABLE students
    ADD COLUMN registration VARCHAR(50),
    ADD COLUMN attendance_rate DOUBLE PRECISION NOT NULL DEFAULT 0;

UPDATE students
SET registration = 'STU-' || UPPER(SUBSTRING(CAST(id AS VARCHAR) FROM 1 FOR 8))
WHERE registration IS NULL;

ALTER TABLE students
    ALTER COLUMN registration SET NOT NULL;

ALTER TABLE students
    ADD CONSTRAINT uk_students_registration UNIQUE (registration),
    ADD CONSTRAINT ck_student_attendance CHECK (attendance_rate BETWEEN 0 AND 100);

ALTER TABLE vacancies
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'OPEN',
    ADD COLUMN manager_id UUID REFERENCES managers(user_id);

ALTER TABLE vacancies
    ADD CONSTRAINT ck_vacancy_status CHECK (status IN ('OPEN', 'CLOSED', 'URGENT'));

CREATE INDEX idx_vacancies_manager ON vacancies (manager_id);
