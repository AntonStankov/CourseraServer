ALTER TABLE question
ADD COLUMN points INT CHECK (points < 0 AND points > 11)