CREATE TABLE tabs (
    tab_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contentType VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    course_id BIGINT,
    FOREIGN KEY (course_id) REFERENCES courses(courseId)
);