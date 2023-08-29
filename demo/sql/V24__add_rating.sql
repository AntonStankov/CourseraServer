CREATE TABLE rating(
    rating_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT,
    course_id BIGINT,
    rating DOUBLE CHECK (rating >= 1 AND rating <= 5)
);