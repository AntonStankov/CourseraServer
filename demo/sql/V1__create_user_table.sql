CREATE TABLE app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255),
    timeCreated DATETIME
);

CREATE TABLE students (
    student_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES app_users(id)
);

CREATE TABLE teachers (
    teacher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES app_users(id)
);

CREATE TABLE courses (
    courseId BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT,
    courseName VARCHAR(255),
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id)
);