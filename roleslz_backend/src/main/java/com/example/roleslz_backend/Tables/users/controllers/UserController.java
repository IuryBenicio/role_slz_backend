package com.example.roleslz_backend.Tables.users.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.users.DTOS.PasswordDTO;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;

    }

    //rotas

    @PatchMapping("edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable long id, @Valid @RequestBody UserDTODetails userDTODetails){
        UserDTODetails user = userService.editUserDetails(userDTODetails, id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("user/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id){
        UserDTODetails user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    @PostMapping("add_f_token/{id}")
    public ResponseEntity<String> addFirebaseToken(@PathVariable long id, @RequestBody String token){
        userService.addFirebaseToken(id,token);
        return ResponseEntity.ok("Token atualizado");
    }

    @PatchMapping("verify_password/{id}")
    public ResponseEntity<String> verifyPassword(@PathVariable long id, @Valid @RequestBody PasswordDTO passwordDTO){
            userService.verifyPassword(id, passwordDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Senha verificada");
    }

    @PatchMapping("verify_email")
    public ResponseEntity<String> verifyPassword(@RequestParam("token") String token){
        userService.verifyUserByEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário verificado");
    }

    @PatchMapping("get-user/{pagina}")
    public ResponseEntity<?> getUsersPages(@PathVariable("pagina") int pagina, @RequestParam("itens") int itens){
        Set<UserDTODetails> response = userService.getUsersPages(pagina,itens);
        return ResponseEntity.status(200).body(response);
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

    @PatchMapping("unconfirm_presence/{userId}/event/{eventId}")
    public ResponseEntity<String> unconfirmPresence(@PathVariable long userId, @PathVariable long eventId, @RequestBody EventoDTO eventoDTO){
        userService.unconfirmPresence(userId, eventId);
        return ResponseEntity.ok("confirmação retirada do evento "+ eventoDTO.title()+ ".");
    }
}
