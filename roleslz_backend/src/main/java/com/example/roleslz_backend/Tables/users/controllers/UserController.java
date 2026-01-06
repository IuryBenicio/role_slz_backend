package com.example.roleslz_backend.Tables.users.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.users.DTOS.PasswordDTO;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;

import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapper;
import com.example.roleslz_backend.Tables.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserDetailsMapper userDetailsMapper;

    public UserController(UserService userService, UserDetailsMapper userDetailsMapper) {
        this.userService = userService;
        this.userDetailsMapper = userDetailsMapper;
    }

    //rotas

    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable long id, @Valid @RequestBody UserDTODetails userDTODetails){
        UserDTODetails user = userService.editUserDetails(userDTODetails, id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("verify_password/{id}")
    public ResponseEntity<String> verifyPassword(@PathVariable long id, @Valid @RequestBody PasswordDTO passwordDTO){
            userService.verifyPassword(id, passwordDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Senha verificada");
    }

    @PatchMapping("edit_password/{id}")
    public ResponseEntity<String> editPassword(@PathVariable long id, @Valid @RequestBody PasswordDTO passwordDTO){
        userService.editPassword(id, passwordDTO);
        return ResponseEntity.ok("Senha editada");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuário deletado");
    }

    @PatchMapping("confirm_presence/{userId}/event/{eventId}")
    public ResponseEntity<String> confirmePresence(@PathVariable long userId, @PathVariable long eventId, @RequestBody EventoDTO eventoDTO){
        userService.confirmPresence(userId, eventId);
        return ResponseEntity.ok("Usuário confirmado no evento "+ eventoDTO.title()+ ".");
    }
}
