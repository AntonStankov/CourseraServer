package com.example.demo.service.user;


import com.example.demo.UserRoleEnum;
import com.example.demo.entity.User;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.config.DataSource;

@NoArgsConstructor(force = true)
@Repository
public class UserTableManager {


    private final DataSource datasource =  new DataSource();

    @Value("${spring.datasource.url}")
    private final String url;

    @Value("${spring.datasource.username}")
    private final String user;

    @Value("${spring.datasource.password}")
    private final String password;


    public UserTableManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }


    public User insertUser(User user) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO app_users (password, email, role, timeCreated) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getPassword());
                preparedStatement.setString(2, user.getEmail());
                preparedStatement.setString(3, user.getRole().toString());
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM app_users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRoleEnum.valueOf(resultSet.getString("role")));
                    user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
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
