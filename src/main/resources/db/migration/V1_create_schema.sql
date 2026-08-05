CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    user_name VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(320) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN', 'MANAGER', 'COORDINATOR')),
    first_login BOOLEAN NOT NULL DEFAULT TRUE,
    token_version BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE admins (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE coordinators (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE managers (
    user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    section VARCHAR(64) NOT NULL
);

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    coordinator_id UUID NOT NULL REFERENCES coordinators(user_id),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE class_entities (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id),
    start_date DATE NOT NULL,
    finish_date DATE NOT NULL,
    status VARCHAR(32) NOT NULL,
    shift_class VARCHAR(32) NOT NULL,
    acronym VARCHAR(50) NOT NULL UNIQUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_class_dates CHECK (finish_date > start_date)
);

CREATE TABLE students (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(320) NOT NULL,
    age BIGINT NOT NULL CHECK (age BETWEEN 16 AND 19),
    average_grade DOUBLE PRECISION,
    status VARCHAR(32) NOT NULL,
    has_seen_email BOOLEAN NOT NULL DEFAULT FALSE,
    status_student VARCHAR(32) NOT NULL DEFAULT 'ENROLLED',
    classentity_id UUID NOT NULL REFERENCES class_entities(id),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_student_average CHECK (average_grade IS NULL OR average_grade >= 0)
);

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    skill_type VARCHAR(32) NOT NULL,
    grade DOUBLE PRECISION NOT NULL,
    student_id UUID NOT NULL REFERENCES students(id),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_skill_grade CHECK (grade >= 0)
);

CREATE TABLE places (
    id UUID PRIMARY KEY,
    place_name VARCHAR(150) NOT NULL,
    park VARCHAR(32) NOT NULL,
    section VARCHAR(64) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE vacancies (
    id UUID PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    numbers_vacancies BIGINT NOT NULL CHECK (numbers_vacancies > 0),
    area VARCHAR(64) NOT NULL,
    shift VARCHAR(32) NOT NULL,
    place_id UUID NOT NULL REFERENCES places(id),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE interviews (
    id UUID PRIMARY KEY,
    interviewer_name VARCHAR(100) NOT NULL,
    date_time TIMESTAMP NOT NULL,
    vacancy_id UUID NOT NULL REFERENCES vacancies(id),
    place_id UUID NOT NULL REFERENCES places(id),
    manager_id UUID NOT NULL REFERENCES managers(user_id),
    student_id UUID NOT NULL UNIQUE REFERENCES students(id),
    reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_users_name_lower ON users (LOWER(name));
CREATE INDEX idx_courses_name_lower ON courses (LOWER(name));
CREATE INDEX idx_students_name_lower ON students (LOWER(name));
CREATE INDEX idx_students_class ON students (classentity_id);
CREATE INDEX idx_skills_student ON skills (student_id);
CREATE INDEX idx_vacancies_place ON vacancies (place_id);
CREATE INDEX idx_interviews_manager ON interviews (manager_id);
CREATE INDEX idx_interviews_pending_reminders
    ON interviews (date_time)
    WHERE reminder_sent = FALSE;