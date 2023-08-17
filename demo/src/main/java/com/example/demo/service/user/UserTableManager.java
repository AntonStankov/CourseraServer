package com.example.demo.service.user;


import com.example.demo.entity.UserSecrets;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.entity.User;
import com.example.demo.service.secrets.SecretTableManager;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final SecretTableManager secretTableManager = new SecretTableManager();


    public User insertUser(User user, String password) {
        Long generatedUserId = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO app_users (email, role, timeCreated) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                preparedStatement.setString(1, user.getPassword());
                preparedStatement.setString(1, user.getEmail());
                preparedStatement.setString(2, user.getRole().toString());
                preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

                int affectedRows = preparedStatement.executeUpdate();


                if (affectedRows > 0) {
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedUserId = generatedKeys.getLong(1);
                        UserSecrets userSecrets = new UserSecrets();
                        userSecrets.setUser_id(getUserById(generatedUserId));
                        userSecrets.setPassword(password);
                        secretTableManager.insertSecret(userSecrets);
                    }
                }
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
                    user.setEmail(resultSet.getString("email"));
                    user.setRole(UserRoleEnum.valueOf(resultSet.getString("role")));
                    user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                    user.setPicturePath(resultSet.getString("picture_path"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(String path, Long id) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE app_users SET picture_path = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, path);
                preparedStatement.setLong(2, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getUserById(id);
    }

    public void deleteUser(Long userId) {
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
//                        user.setPassword(resultSet.getString("password"));
                        user.setEmail(resultSet.getString("email"));
                        user.setRole(UserRoleEnum.valueOf(resultSet.getString("role")));
                        user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                        user.setPicturePath(resultSet.getString("picture_path"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserById(Long id) {
        User user = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM app_users WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.setId(resultSet.getLong("id"));
//                        user.setPassword(resultSet.getString("password"));
                        user.setEmail(resultSet.getString("email"));
                        user.setRole(UserRoleEnum.valueOf(resultSet.getString("role")));
                        user.setTimeCreated(resultSet.getTimestamp("timeCreated").toLocalDateTime());
                        user.setPicturePath(resultSet.getString("picture_path"));

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User changeEmail(Long userId, String email){
        User user = null;
        try (Connection connection = datasource.createConnection()) {
            String sql = "UPDATE app_users SET email = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setLong(2, userId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getUserById(userId);
    }
}
