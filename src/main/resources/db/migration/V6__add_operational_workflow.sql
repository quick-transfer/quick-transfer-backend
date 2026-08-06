CREATE TABLE operational_shifts (
    id UUID PRIMARY KEY,
    code VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    supervisor_name VARCHAR(160),
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_operational_shift_capacity CHECK (capacity > 0)
);

INSERT INTO operational_shifts (id, code, name, supervisor_name, capacity)
VALUES
    ('00000000-0000-0000-0000-000000000101', 'TRN-A', 'Turno A - Matutino', NULL, 50),
    ('00000000-0000-0000-0000-000000000102', 'TRN-B', 'Turno B - Vespertino', NULL, 50),
    ('00000000-0000-0000-0000-000000000103', 'TRN-C', 'Turno C - Noturno', NULL, 30);

ALTER TABLE students ADD COLUMN operational_shift_id UUID REFERENCES operational_shifts(id);

UPDATE students s
SET operational_shift_id = CASE c.shift_class
    WHEN 'MORNING' THEN '00000000-0000-0000-0000-000000000101'::UUID
    WHEN 'AFTERNOON' THEN '00000000-0000-0000-0000-000000000102'::UUID
    ELSE '00000000-0000-0000-0000-000000000103'::UUID
END
FROM class_entities c
WHERE c.id = s.classentity_id;

ALTER TABLE students ALTER COLUMN operational_shift_id SET NOT NULL;
CREATE INDEX idx_students_operational_shift ON students (operational_shift_id);

CREATE TABLE shift_transfer_requests (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    current_shift_id UUID NOT NULL REFERENCES operational_shifts(id),
    target_shift_id UUID NOT NULL REFERENCES operational_shifts(id),
    requested_by UUID NOT NULL REFERENCES users(id),
    resolved_by UUID REFERENCES users(id),
    reason TEXT NOT NULL,
    resolution_notes TEXT,
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_shift_transfer_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT ck_shift_transfer_distinct CHECK (current_shift_id <> target_shift_id)
);

CREATE INDEX idx_shift_transfer_student ON shift_transfer_requests (student_id);
CREATE INDEX idx_shift_transfer_status ON shift_transfer_requests (status);

CREATE TABLE system_settings (
    id BIGINT PRIMARY KEY,
    default_shift_capacity INTEGER NOT NULL,
    high_demand_percentage INTEGER NOT NULL,
    email_sender VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by UUID REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_settings_capacity CHECK (default_shift_capacity > 0),
    CONSTRAINT ck_settings_percentage CHECK (high_demand_percentage BETWEEN 1 AND 100)
);

INSERT INTO system_settings (
    id, default_shift_capacity, high_demand_percentage, email_sender, updated_at)
VALUES (1, 50, 85, 'notificacoes.quicktransfer@weg.net', CURRENT_TIMESTAMP);
