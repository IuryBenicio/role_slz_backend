package com.example.roleslz_backend.JWT.controller;
import com.example.roleslz_backend.JWT.UserDetailsImpl;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.infra.service.SecurityService;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTOLogin;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
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

    public AuthController(AuthenticationManager authenticationManager, UserService userService, SecurityService tokenService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @PostMapping("login")
    public ResponseEntity<Map> login(@RequestBody @Valid UserDTOLogin userLoginDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password());
        var auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginDTO.email(), userLoginDTO.password())
        );
        UserEntity userEntity = ((UserDetailsImpl) auth.getPrincipal()).getUser();

        var token = tokenService.generateToken(userEntity);

        return ResponseEntity.ok(Map.of("token", token, "message", "Login feito com sucesso"));
    }

    @PostMapping("register")
    public ResponseEntity<Map> RegisterUser (@RequestBody @Valid UserDTORegister userDTO){

        UserEntity response = userService.addNewUserService(userDTO);
        String token = tokenService.generateToken(response);

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("token", token,"message", "Usuário registrado"));
    }

    @GetMapping("verify?token={token}")
    public ResponseEntity<String> verifyEmailToken (@RequestParam("token") String token){
        emailService.VerifyTokenEmail(token);
        return ResponseEntity.status(200).body("Conta ativada!");
    }
}
