package com.example.roleslz_backend.Tables.users.controllers;

import com.example.roleslz_backend.Tables.users.DTOS.PasswordDTO;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserEditDTO;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserProjectionProfile;
import com.example.roleslz_backend.Tables.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    // Perfil completo do PRÓPRIO usuário logado (id vem do token, nunca do corpo).
    // Antes era @GetMapping com @RequestBody — o Spring ignora corpo em GET — e o service
    // buscava findByNome(dto.getEmail()), o que devolveria o perfil de outra pessoa.
    @GetMapping("user_profile")
    public ResponseEntity<UserProjectionProfile> getUserProfile(){
        return ResponseEntity.ok(userService.getMeuPerfil());
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

    // Sem {id}: perguntar isso sobre outra pessoa é vazamento de existência de conta.
    @GetMapping("user_has_event")
    public ResponseEntity<Boolean> userHasEvent(){
        return ResponseEntity.ok(userService.userHasEvent());
    }

    @GetMapping("user_has_business")
    public ResponseEntity<Boolean> userHasBusiness(){
        return ResponseEntity.ok(userService.userHasBusiness());
    }

    @PatchMapping("verify_email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token){
        userService.verifyUserByEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário verificado");
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

    // Usuário vem do JWT e o evento do path — sem corpo.
    @PatchMapping("confirm_presence/event/{eventId}")
    public ResponseEntity<String> confirmePresence(@PathVariable long eventId){
        userService.confirmPresence(eventId);
        return ResponseEntity.ok("Presença confirmada no evento " + eventId + ".");
    }

    @PatchMapping("unconfirm_presence/event/{eventId}")
    public ResponseEntity<String> unconfirmPresence(@PathVariable long eventId){
        userService.unconfirmPresence(eventId);
        return ResponseEntity.ok("Confirmação retirada do evento " + eventId + ".");
    }
}
