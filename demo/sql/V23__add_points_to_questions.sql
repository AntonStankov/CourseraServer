ALTER TABLE question
ADD COLUMN points INT CHECK (points >= 1 AND points <= 10);
