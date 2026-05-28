package com.example.roleslz_backend.JWT.controller;
import com.example.roleslz_backend.JWT.UserDetailsImpl;
import com.example.roleslz_backend.Tables.users.DTOS.*;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.infra.service.SecurityService;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityService tokenService;
    private final EmailService emailService;
    private final UserDetailsMapperClass userDetailsMapperClass;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, SecurityService tokenService, EmailService emailService, UserDetailsMapperClass userDetailsMapperClass) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.userDetailsMapperClass = userDetailsMapperClass;
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid UserDTOLogin userLoginDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());
        var auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password())
        );
        UserEntity userEntity = ((UserDetailsImpl) auth.getPrincipal()).getUser();

        var token = tokenService.generateToken(userEntity);

        UserDTODetailsWithId userResponse = userDetailsMapperClass.toDTOWithId(userEntity);

        return ResponseEntity.ok(Map.of("token", token, "message", "Login feito com sucesso", "user", userResponse));
    }

    @PostMapping("register")
    public ResponseEntity<Map<String, String>> RegisterUser (@RequestBody @Valid UserDTORegister userDTO){

        UserEntity response = userService.addNewUserService(userDTO);
        String token = tokenService.generateToken(response);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token,"message", "Usuário registrado"));
    }

    @PostMapping("register_google")
    public ResponseEntity<Map<String, String>> RegisterUserGoogle (@RequestBody @Valid UserDTORegisterSocial dto){

        UserEntity response = userService.addNewUserGoogle(dto);
        String token = tokenService.generateToken(response);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token,"message", "Usuário registrado através do google"));
    }

    @GetMapping("verify?token={token}")
    public ResponseEntity<String> verifyEmailToken (@RequestParam("token") String token){
        emailService.VerifyTokenEmail(token);
        return ResponseEntity.status(200).body("Conta ativada!");
    }
}
