package com.example.demo.service.course;


import com.example.demo.config.DataSource;
import com.example.demo.entity.Course;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.service.teacher.TeacherService;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class CoursesTableManager {

    private UserService userService = new UserService() {
        @Override
        public User save(User user, String password) {
            return UserService.super.save(user, password);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }
    };

    private TeacherService teacherService = new TeacherService() {
        @Override
        public Teacher save(Teacher teacher, User user) {
            return TeacherService.super.save(teacher, user);
        }

        @Override
        public Teacher findById(Long id) {
            return TeacherService.super.findById(id);
        }

        @Override
        public Teacher findByUserId(Long userId) {
            return TeacherService.super.findByUserId(userId);
        }
    };

    private final DataSource datasource = new DataSource();
    public Course insertCourse(Course course, Teacher teacher) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO courses (courseName, credit) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, course.getCourseName());
                preparedStatement.setInt(2, course.getCredit());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        Long generatedCourseId = generatedKeys.getLong(1);
                        course.setCourseId(generatedCourseId);
                    }
                }
            }

            // Associate the User with the Teacher
            associateCourseWithTeacher(connection, course.getCourseId(), teacher.getTeacher_id());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    private void associateCourseWithTeacher(Connection connection, Long courseId, Long teacherId) {
        String sql = "UPDATE courses SET teacher_id = ? WHERE courseId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, teacherId);
            preparedStatement.setLong(2, courseId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Course getCourseById(Long courseId, Long userId, String email) {
        Course course = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT c.*, t.* FROM courses c " +
                    "JOIN teachers t ON c.teacher_id = t.teacher_id " +
                    "WHERE c.courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        course = new Course();
                        course.setCourseId(resultSet.getLong("courseId"));
                        course.setCourseName(resultSet.getString("courseName"));
                        course.setCredit(resultSet.getInt("credit"));

                        // Create a Teacher object and set its attributes
                        Teacher teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        // Set other teacher attributes

                        // Set the Teacher object for the Course
                        course.setTeacher(teacher);
                    }
                }
            }
        } catch (SQLException e) {
            // Handle the exception
        }
        return course;
    }

    public void updateCourse(Course course) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE courses SET courseName = ? credit = ? WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, course.getCourseName());
                preparedStatement.setInt(2, course.getCredit());
                preparedStatement.setLong(3, course.getCourseId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(Long courseId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM courses WHERE courseId = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, courseId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
