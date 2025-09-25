--liquibase formatted sql

-- changeset dev:2.0-add-age-to-person-oracle
--precondition-dbms type:oracle
ALTER TABLE person ADD (age NUMBER);
UPDATE person SET age = 30 WHERE name = 'Alice';
UPDATE person SET age = 25 WHERE name = 'Bob';
ALTER TABLE person MODIFY (age DEFAULT 18);
ALTER TABLE person ADD CONSTRAINT chk_person_age CHECK (age >= 0);

-- changeset dev:2.0-add-age-to-person-clickhouse
--precondition-dbms type:clickhouse
ALTER TABLE person ADD COLUMN age UInt32;
ALTER TABLE person UPDATE age = 30 WHERE name = 'Alice';
ALTER TABLE person UPDATE age = 25 WHERE name = 'Bob';
-- ClickHouse does not support ALTER COLUMN DEFAULT or check constraints.
