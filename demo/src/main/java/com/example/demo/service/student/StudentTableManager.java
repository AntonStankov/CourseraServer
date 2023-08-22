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
        public User save(User user, String password) {
            return UserService.super.save(user, password);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }
    };


    public Student insertStudent(Student student, User user) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO students (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, student.getName());
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

    public Student getStudentById(Long studentId) {
        Student student = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM students s " +
                    "JOIN app_users a ON s.user_id = a.id " +
                    "WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, studentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setName(resultSet.getString("name"));
                        student.setUser(userService.findByEmail(resultSet.getString("email")));
                        student.setCredit(resultSet.getLong("credit"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }


    public Student getStudentByUserId(Long userId) {
        Student student = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM students s " +
                    "JOIN app_users a ON s.user_id = a.id " +
                    "WHERE s.user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student();
                        student.setStudent_id(resultSet.getLong("student_id"));
                        student.setName(resultSet.getString("name"));
                        student.setUser(userService.findByEmail(resultSet.getString("email")));
                        student.setCredit(resultSet.getLong("credit"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public Student updateStudentName(String name, Long id) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE students SET name = ? WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setLong(2, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getStudentById(id);
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

    public void addCredit(Long studentId, int credit){
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE students SET credit = ? WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                Student student = getStudentById(studentId);
                preparedStatement.setLong(1, student.getCredit() + credit);
                preparedStatement.setLong(2, studentId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
