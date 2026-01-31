package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.DTOS.PasswordDTO;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTOLogin;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.*;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapper;
import com.example.roleslz_backend.Tables.users.mapper.UserRegisterMapper;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.infra.service.SecurityService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserRegisterMapper userRegisterMapper;
    private final UserDetailsMapper userDetailsMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventoRepository eventoRepository;
    private final SecurityService securityService;
    private final EmailService emailService;


    public UserService(UserRepository userRepository, UserRegisterMapper userRegisterMapper, UserDetailsMapper userDetailsMapper, PasswordEncoder passwordEncoder, EventoRepository eventoRepository, SecurityService securityService, EmailService emailService) {
        this.userRepository = userRepository;
        this.userRegisterMapper = userRegisterMapper;
        this.userDetailsMapper = userDetailsMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventoRepository = eventoRepository;
        this.securityService = securityService;
        this.emailService = emailService;
    }

    public UserEntity addNewUserService(UserDTORegister userDTORegister){
        boolean userExists = userRepository.findByEmail(userDTORegister.email()).isPresent();
        if (userExists){
            throw new UserExists("Este email já está vinculado a um usuário!");
        }

        String hashedPassword = passwordEncoder.encode(userDTORegister.password());

        UserEntity user = userRegisterMapper.toEntity(userDTORegister);

        user.setEnable(false);
        user.setPassword(hashedPassword);

        String token = securityService.generateToken(user);

        emailService.sendVerificationEmail(user.getEmail(), token);

        try{
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserNotCreated("Usuário não criado");
        }
    }

    public UserEntity getUser(UserDTOLogin userDTOLogin){
        UserEntity user = userRepository.findByEmail(userDTOLogin.email()).orElseThrow(()->new UserExists("Não encontramos nenhum usuário ligado a esse email"));

        Boolean verificaSenha = passwordEncoder.matches(userDTOLogin.password(), user.getPassword());

        if(!verificaSenha){
            throw new PasswordDoesntMatch("Senha incorreta");
        }

        return user;
    }

    public UserDTODetails editUserDetails(UserDTODetails userDTODetails, long id){
        UserEntity userBefore = userRepository.findById(id).orElseThrow(()->new UserDontExists("Usuário não encontrado"));

        userDetailsMapper.updateEntityFromDto(userDTODetails, userBefore);

        try{
            userRepository.save(userBefore);
            return userDetailsMapper.toDTO(userBefore);
        } catch (Exception e) {
            throw new UserNotEdited("Usuário não editado");
        }
    }

    public Boolean verifyPassword(long id, PasswordDTO data){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotEdited("Senha não editada"));

        Boolean verificaSenha = passwordEncoder.matches(data.password(), user.getPassword());

        if (!verificaSenha){
            throw new PasswordDoesntMatch("Senhas não coincidem!");
        }
        else {
            return true;
        }
    }

    @Transactional
    public void addFirebaseToken(long id, String token){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new UserNotFounded("Usuário não encontrado"));
        try{
            user.setFcmToken(token);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserNotEdited("Usuário não editado" + e);
        }
    }

    public void verifyUserByEmail(String token){
        String email = securityService.validateToken(token);

        if(email.isEmpty()){
            throw new InvalidToken("Token inválido");
        }

        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFounded("Usuário não encontrado pelo email"));
            user.setEnable(true);
            userRepository.save(user);
    }

    public void editPassword(long id ,PasswordDTO data){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotEdited("Senha não editada"));

        Boolean verificaSenha = passwordEncoder.matches(data.password(), user.getPassword());

        if(verificaSenha){
            throw new PasswordDoesntMatch("Senhas igual a sua atual. Digite uma nova senha!");
        }

        String hashedPassword = passwordEncoder.encode(data.password());

        try{
            user.setPassword(hashedPassword);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserNotEdited("Senha não alterada");
        }

    }

    public void deleteUser(long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserDontExists("usuário não encontrado"));
        try{
            userRepository.delete(user);
        } catch (Exception e) {
            throw new UserNotDeleted("Usuário não deletado");
        }
    }

    //por ser many to many, é necessário usar a anotation @Transactional para poder efetuar a persistencia das duas tabelas relativas criadas
    @Transactional
    public void confirmPresence(long userId, long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        try{
        evento.getConfirmacoes().add(user);
        user.getEventos().add(evento);

        userRepository.save(user);
        eventoRepository.save(evento);
        } catch (Exception e) {
            throw new PresenceNotConfirmated("Presença não confirmada" + e.getMessage());
        }
    }

    @Transactional
    public void unconfirmPresence(long userId, long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));
        UserEntity user = userRepository.findById(userId).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        try{
            if(evento.getConfirmacoes().contains(user)){
                evento.getConfirmacoes().remove(user);
            }
            if(user.getEventos().contains(evento)){
                user.getEventos().remove(evento);
            }

            userRepository.save(user);
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new PresenceNotConfirmated("Presença não retirada" + e.getMessage());
        }
    }
}
