package com.example.demo.service.user;


import com.example.demo.entity.User;
import org.springframework.stereotype.Service;


@Service
public interface UserService {

    UserTableManager tableManager = new UserTableManager();

    public default User save(User user){
        return tableManager.insertUser(user);
    }

    public default User findByEmail(String email){
        return tableManager.getUserByEmail(email);
    }

    public default User updateUser(User user, Long id){
        return tableManager.updateUser(user, id);
    }

    public default User findUserById(Long id){
        return tableManager.getUserById(id);
    }

    public default void deleteUser(Long id){
        tableManager.deleteUser(id);
    }

}
