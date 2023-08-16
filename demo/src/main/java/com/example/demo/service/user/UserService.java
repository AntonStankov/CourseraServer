package com.example.demo.service.user;


import com.example.demo.entity.User;
import org.springframework.stereotype.Service;


@Service
public interface UserService {

    UserTableManager tableManager = new UserTableManager();

    public default User save(User user, String password){
        return tableManager.insertUser(user, password);
    }

    public default User findByEmail(String email){
        return tableManager.getUserByEmail(email);
    }

//    public default User updateUser(User user, Long id){
//        return tableManager.updateUser(user, id);
//    }

    public default User findUserById(Long id){
        return tableManager.getUserById(id);
    }

    public default void deleteUser(Long id){
        tableManager.deleteUser(id);
    }

    public default User changeEmil(Long id, String email){
        return tableManager.changeEmail(id, email);
    }

    public default void setProfilePic(String path, Long userId){
        tableManager.updateUser(path, userId);
    }

}
