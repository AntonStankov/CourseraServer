package com.example.demo.service.teacher;

import com.example.demo.config.DataSource;
import com.example.demo.entity.Teacher;
import com.example.demo.entity.User;
import com.example.demo.service.user.UserService;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class TeacherTableManager {

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



    public Teacher insertTeacher(Teacher teacher, User user) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO teachers (name) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, teacher.getName());
                int affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        Long generatedTeacherId = generatedKeys.getLong(1);
                        teacher.setTeacher_id(generatedTeacherId);
                    }
                }
            }

            // Associate the User with the Teacher
            associateUserWithTeacher(connection, teacher.getTeacher_id(), user.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }

    private void associateUserWithTeacher(Connection connection, Long teacherId, Long userId) {
        String sql = "UPDATE teachers SET user_id = ? WHERE teacher_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, teacherId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Teacher getTeacherById(Long teacherId) {
        Teacher teacher = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM teachers  t " +
                    "JOIN app_users a ON a.id = t.user_id " +
                    "WHERE teacher_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, teacherId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        teacher.setUser(userService.findByEmail(resultSet.getString("email")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }


    public Teacher getTeacherByUserId(Long userId, String email) {
        Teacher teacher = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM teachers WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, userId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        teacher = new Teacher();
                        teacher.setTeacher_id(resultSet.getLong("teacher_id"));
                        teacher.setName(resultSet.getString("name"));
                        teacher.setUser(userService.findByEmail(email));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teacher;
    }

    public Teacher updateTeacher(Teacher teacher) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE teachers SET name = ? WHERE teacher_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, teacher.getName());
                preparedStatement.setLong(2, teacher.getTeacher_id());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getTeacherById(teacher.getTeacher_id());
    }

    public void deleteTeacher(Long teacherId) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "DELETE FROM teachers WHERE teacher_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, teacherId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}