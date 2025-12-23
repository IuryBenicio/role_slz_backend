package com.example.roleslz_backend.users.services;

import com.example.roleslz_backend.users.DTOS.UserDTOLogin;
import com.example.roleslz_backend.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.users.entity.UserEntity;
import com.example.roleslz_backend.users.exceptions.PasswordDoesntMatch;
import com.example.roleslz_backend.users.exceptions.UserExists;
import com.example.roleslz_backend.users.mapper.UserRegisterMapper;
import com.example.roleslz_backend.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    UserRegisterMapper userRegisterMapper;

    public UserEntity addNewUserService(UserDTORegister userDTORegister){
        boolean userExists = userRepository.findByEmail(userDTORegister.email()).isPresent();
        if (userExists = true){
            throw new UserExists("Este email já está vinculado a um usuário!");
        }
        //password
        UserEntity user = userRepository.save(userRegisterMapper.toEntity(userDTORegister));

        return user;
    }

    public UserEntity getUser(UserDTOLogin userDTOLogin){
        UserEntity user = userRepository.findByEmail(userDTOLogin.email()).orElseThrow(()->new UserExists("Não encontramos nenhum usuário ligado a esse email"));

        if(!userDTOLogin.password().equalsIgnoreCase(user.getPassword())){
            throw new PasswordDoesntMatch("Senha incorreta");
        }

        return user;
    }
}
