package com.example.roleslz_backend.Tables.users.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.users.DTOS.PasswordDTO;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserDTOProfileReq;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserEditDTO;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserProjectionProfile;
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

    @PatchMapping("edit")
    public ResponseEntity<UserEditDTO> editUser(@Valid @RequestBody UserEditDTO dto){
        UserEditDTO user = userService.editUserDetails(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping("hello")
    public ResponseEntity<String> helloCheck(){
        return ResponseEntity.ok("Checado!");
    }

    @GetMapping("user")
    public ResponseEntity<UserDTODetails> getUser(){
        UserDTODetails user = userService.getUserById();
        return ResponseEntity.ok(user);
    }

    @GetMapping("user_profile")
    public ResponseEntity<UserProjectionProfile> getUserProfile(@RequestBody UserDTOProfileReq dto){
        UserProjectionProfile user = userService.getUserProfile(dto);
        return ResponseEntity.ok(user);
    }


    @PostMapping("add_f_token")
    public ResponseEntity<String> addFirebaseToken(@RequestBody String token){
        userService.addFirebaseToken(token);
        return ResponseEntity.ok("Token atualizado");
    }

    @PatchMapping("verify_password")
    public ResponseEntity<String> verifyPassword(@Valid @RequestBody PasswordDTO passwordDTO){
            userService.verifyPassword(passwordDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Senha verificada");
    }

    @GetMapping("verify_email/{email}")
    public ResponseEntity<String> verifyEmailByEmail(@PathVariable String email){
        userService.verifyEmail(email);
        return ResponseEntity.ok("Email verificado");
    }

    @GetMapping("user_has_event/{id}")
    public ResponseEntity<Boolean> userHasEvent(@PathVariable("id") long id){
        boolean response = userService.userHasEvent(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("user_has_business/{id}")
    public ResponseEntity<Boolean> userHasBusiness(@PathVariable("id") long id){
        boolean response = userService.userHasBusiness(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("verify_email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        userService.verifyUserByEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário verificado");
    }

    @GetMapping("get-user/{pagina}")
    public ResponseEntity<Set<UserDTODetails>> getUsersPages(@PathVariable("pagina") int pagina, @RequestParam("itens") int itens){
        Set<UserDTODetails> response = userService.getUsersPages(pagina,itens);
        return ResponseEntity.status(200).body(response);
    }


    @PatchMapping("edit_password")
    public ResponseEntity<String> editPassword(@Valid @RequestBody PasswordDTO passwordDTO){
        userService.editPassword(passwordDTO);
        return ResponseEntity.ok("Senha editada");
    }

    @DeleteMapping("delete")
    public ResponseEntity<String> deleteUser(){
        userService.deleteUser();
        return ResponseEntity.ok("Usuário deletado");
    }

    @PatchMapping("confirm_presence/event/{eventId}")
    public ResponseEntity<String> confirmePresence(@PathVariable long eventId, @RequestBody EventoDTO eventoDTO){
        userService.confirmPresence(eventId);
        return ResponseEntity.ok("Usuário confirmado no evento "+ eventoDTO.title()+ ".");
    }

    @PatchMapping("unconfirm_presence/event/{eventId}")
    public ResponseEntity<String> unconfirmPresence(@PathVariable long userId, @PathVariable long eventId, @RequestBody EventoDTO eventoDTO){
        userService.unconfirmPresence(eventId);
        return ResponseEntity.ok("confirmação retirada do evento "+ eventoDTO.title()+ ".");
    }
}
