package com.example.demo.service.student;

import com.example.demo.config.DataSource;
import com.example.demo.entity.Student;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class StudentTableManager {

    private final DataSource datasource = new DataSource();

    private UserService userService = new UserService() {
        @Override
        public User save(User user) {
            return UserService.super.save(user);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }
    };


    public Student insertStudent(Student student, User user) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO students (firstName, lastName) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, student.getFirstName());
                preparedStatement.setString(2, student.getLastName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        Long generatedStudentId = generatedKeys.getLong(1);
                        student.setStudent_id(generatedStudentId);
                    }
                }
            }

            // Associate the User with the Teacher
            associateUserWithStudent(connection, student.getStudent_id(), user.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    private void associateUserWithStudent(Connection connection, Long studentId, Long userId) {
        String sql = "UPDATE students SET user_id = ? WHERE student_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, studentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Student getStudentById(Long studentId, String email) {
        Student student = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, studentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setFirstName(resultSet.getString("firstName"));
                        student.setLastName(resultSet.getString("lastName"));
                        student.setUser(userService.findByEmail(email));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }


    public Student getStudentByUserId(Long userId, String email) {
        Student student = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM students WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setFirstName(resultSet.getString("firstName"));
                        student.setLastName(resultSet.getString("lastName"));
                        student.setUser(userService.findByEmail(email));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public void updateStudentFirstName(Student student) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE students SET first_name = ? WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, student.getFirstName());
                preparedStatement.setLong(2, student.getStudent_id());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStudentLastName(Student student) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE students SET last_name = ? WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, student.getLastName());
                preparedStatement.setLong(2, student.getStudent_id());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTeacher(Long studentId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, studentId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
