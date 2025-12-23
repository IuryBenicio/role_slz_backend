package com.example.roleslz_backend.authController;

import com.example.roleslz_backend.JWT.UserDetailsImpl;
import com.example.roleslz_backend.users.DTOS.UserDTOLogin;
import com.example.roleslz_backend.users.entity.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class authController {



    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody @Valid UserDTOLogin userDTOLogin){
        var usernamePassword = new UsernamePasswordAuthenticationToken(userDTOLogin.email(), userDTOLogin.password());
        var auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTOLogin.email(), userDTOLogin.password())
        );

        UserEntity userEntity = ((UserDetailsImpl) auth.getPrincipal()).getUser();


        var token = tokenService.generateToken(userEntity);


        return ResponseEntity.ok(Map.of("token", token));    }

}
