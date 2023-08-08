package com.example.demo.service.secrets;

import com.example.demo.config.DataSource;
import com.example.demo.entity.User;
import com.example.demo.entity.UserSecrets;
import com.example.demo.enums.UserRoleEnum;
import com.example.demo.service.user.UserService;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@Repository
public class SecretTableManager {
    private final DataSource datasource =  new DataSource();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService = new UserService() {
        @Override
        public User save(User user, String password) {
            return UserService.super.save(user, password);
        }

        @Override
        public User findByEmail(String email) {
            return UserService.super.findByEmail(email);
        }

        @Override
        public User findUserById(Long id) {
            return UserService.super.findUserById(id);
        }

        @Override
        public void deleteUser(Long id) {
            UserService.super.deleteUser(id);
        }

        @Override
        public User changeEmil(Long id, String email) {
            return UserService.super.changeEmil(id, email);
        }
    };

    public UserSecrets insertSecret(UserSecrets userSecrets) {
        try (Connection connection = datasource.createConnection()) {
            String sql = "INSERT INTO user_secrets (user_id, password) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setLong(1, userSecrets.getUser_id().getId());
                preparedStatement.setString(2, passwordEncoder.encode(userSecrets.getPassword()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userSecrets;
    }


    public UserSecrets getSecretsById(Long id) {
        UserSecrets user = new UserSecrets();
        try (Connection connection = datasource.createConnection()) {
            String sql = "SELECT * FROM user_secrets WHERE user_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user.setUser_id(userService.findUserById(id));
                        user.setPassword(resultSet.getString("password"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
