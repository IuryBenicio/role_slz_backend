package com.example.roleslz_backend.users.controllers;

import com.example.roleslz_backend.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.users.entity.UserEntity;
import com.example.roleslz_backend.users.services.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "user")
public class UserController {

    @Autowired
    UserService userService;


    //rotas
    @PostMapping("")
    public ResponseEntity<String> addNewUser(@NotBlank @RequestBody UserDTORegister userDTORegister){
        UserEntity user = userService.addNewUserService(userDTORegister);
    }
}
