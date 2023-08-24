CREATE TABLE tabCompletion(
    completion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    tab_id BIGINT,
    FOREIGN KEY (student_id) REFERENCES students (student_id),
    FOREIGN KEY (tab_id) REFERENCES tabs (tab_id)
);