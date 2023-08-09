ALTER TABLE app_users
DROP COLUMN credit;

ALTER TABLE students
ADD COLUMN credit BIGINT;
