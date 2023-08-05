package com.example.demo.service.enrollment;


import com.example.demo.UserRoleEnum;
import com.example.demo.config.DataSource;
import com.example.demo.entity.*;
import com.example.demo.service.course.CourseService;
import com.example.demo.service.student.StudentService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(force = true)
@Repository
public class EnrollmentTableManager {


    private DataSource datasource = new DataSource();

    private CourseService courseService = new CourseService() {
        @Override
        public Course findById(Long courseId, Long userId, String email) {
            return CourseService.super.findById(courseId, userId, email);
        }
    };

    private StudentService studentService = new StudentService() {
        @Override
        public Student save(Student student, User user) {
            return StudentService.super.save(student, user);
        }

        @Override
        public Student findById(Long id, String email) {
            return StudentService.super.findById(id, email);
        }
    };


    public Enrollment insertEnrollment(Long studentId, Long courseId) {
        Long generatedEnrollmentId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO enrollment (student_id, course_id, completion_date) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.setLong(2, courseId);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                int affectedRows = preparedStatement.executeUpdate();



                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedEnrollmentId = generatedKeys.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getEnrollmentById(generatedEnrollmentId);
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> enrollments = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM enrollment";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Enrollment enrollment = new Enrollment();
                    enrollment.setEnrollment_id(resultSet.getLong("enrollment_id"));
                    enrollment.setCourse(resultSet.getObject("course_id", Course.class));
                    enrollment.setStudent(resultSet.getObject("student_id", Student.class));
                    enrollment.setCompletion_date(resultSet.getTimestamp("completion_date").toLocalDateTime());
                    enrollments.add(enrollment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enrollments;
    }


    public Enrollment getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM enrollment e " +
                    "JOIN students s ON e.student_id = s.student_id " +
                    "JOIN courses c ON e.course_id = c.courseId " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE e.enrollment_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, enrollmentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        enrollment = new Enrollment();
                        enrollment.setEnrollment_id(resultSet.getLong("enrollment_id"));
                        enrollment.setCompletion_date(resultSet.getTimestamp("completion_date").toLocalDateTime());
                        Student student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setFirstName(resultSet.getString("firstName"));
                        student.setLastName(resultSet.getString("lastName"));
                        enrollment.setStudent(student);
                        Course course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        course.setTeacher(teacher);
                        enrollment.setCourse(course);





                        enrollment.setCourse(course);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return enrollment;
    }

    public void updateUser(User user) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE app_users SET password = ?, email = ?, role = ?, timeCreated = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getPassword());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getRole().name());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(user.getTimeCreated()));
                preparedStatement.setLong(5, user.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(long userId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM app_users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUserByEmail(String email) {
        User user = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM app_users WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.setId(resultSet.getLong("id"));
                        user.setPassword(resultSet.getString("password"));
                        user.setEmail(resultSet.getString("email"));
                        user.setRole(UserRoleEnum.valueOf(resultSet.getString("role")));
                        user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
