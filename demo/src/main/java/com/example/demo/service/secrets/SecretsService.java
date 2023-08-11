package com.example.demo.service.secrets;


import com.example.demo.entity.UserSecrets;
import org.jvnet.hk2.annotations.Service;

@Service
public interface SecretsService {

    SecretTableManager secretTableManager = new SecretTableManager();

    public default UserSecrets save(UserSecrets userSecrets){
        return secretTableManager.insertSecret(userSecrets);
    }

    public default UserSecrets findById(Long id){
        return secretTableManager.getSecretsById(id);
    }

    public default void changePassword(String password, Long userId){
        secretTableManager.updateSecrets(password, userId);
    }
}
