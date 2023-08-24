CREATE TABLE quiz (
    quiz_id BIGINT PRIMARY KEY AUTO_INCREMENT
);

CREATE TABLE question (
    question_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quiz_id BIGINT,
    question VARCHAR(255),
    rightAnswer VARCHAR(255),
    FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id)
);

CREATE TABLE answers (
    answer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    answer VARCHAR(255),
    question_id BIGINT,
    FOREIGN KEY (question_id) REFERENCES question(question_id)
);

ALTER TABLE courses
ADD COLUMN quiz_id BIGINT,
ADD FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id);
