package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.users.DTOS.*;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserDTOProfileReq;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserEditDTO;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserProjectionProfile;
import com.example.roleslz_backend.Tables.users.entity.AuthProvider;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.mapper.UserRegisterMapperClass;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.*;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.GoogleService;
import com.example.roleslz_backend.infra.service.SecurityService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserRegisterMapperClass userRegisterMapper;
    private final UserDetailsMapperClass userDetailsMapper;
    private final PasswordEncoder passwordEncoder;
    private final EventoRepository eventoRepository;
    private final SecurityService securityService;
    private final EmailService emailService;
    private final GoogleService googleService;

    public UserService(UserRepository userRepository, UserRegisterMapperClass userRegisterMapper, UserDetailsMapperClass userDetailsMapper, PasswordEncoder passwordEncoder, EventoRepository eventoRepository, SecurityService securityService, EmailService emailService, GoogleService googleService) {
        this.userRepository = userRepository;
        this.userRegisterMapper = userRegisterMapper;
        this.userDetailsMapper = userDetailsMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventoRepository = eventoRepository;
        this.securityService = securityService;
        this.emailService = emailService;
        this.googleService = googleService;
    }

    @Transactional
    public UserEntity addNewUserService(UserDTORegister userDTORegister){
        boolean userExists = userRepository.findByEmail(userDTORegister.email()).isPresent();
        if (userExists){
            throw new UserExists("Este email já está vinculado a um usuário!");
        }

        String hashedPassword = passwordEncoder.encode(userDTORegister.password());

        UserEntity user = userRegisterMapper.toEntity(userDTORegister);

        String tokenVerificacao = java.util.UUID.randomUUID().toString();
        user.setVerificationToken(tokenVerificacao);

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

    public UserEntity addNewUserGoogle(UserDTORegisterSocial dto){
        var payload = googleService.validarToken(dto.providerId());

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String nome = (String) payload.get("given_name"); // Pega só o primeiro nome do Google

        UserEntity novoUsuario = new UserEntity();
        novoUsuario.setEmail(email);
        novoUsuario.setNome(nome);
        novoUsuario.setSobrenome(dto.sobrenome());
        novoUsuario.setIdade(dto.idade());
        novoUsuario.setSexo(dto.sexo());
        novoUsuario.setAuthProvider(AuthProvider.GOOGLE);
        novoUsuario.setProviderId(googleId);
        novoUsuario.setEnable(true);

        novoUsuario.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        return userRepository.save(novoUsuario);
    }

    @Transactional
    public UserEntity getUser(UserDTOLogin userDTOLogin){
        UserEntity user = userRepository.findByEmail(userDTOLogin.email()).orElseThrow(()->new UserExists("Não encontramos nenhum usuário ligado a esse email"));

        Boolean verificaSenha = passwordEncoder.matches(userDTOLogin.password(), user.getPassword());

        if(!verificaSenha){
            throw new PasswordDoesntMatch("Senha incorreta");
        }

        return user;
    }

    public UserProjectionProfile getUserProfile(UserDTOProfileReq dto){
        UserProjectionProfile user = userRepository.findByNome(dto.getEmail()).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        if(user == null){
            throw new UserNotFounded("Usuário não encontrado");
        }

        return user;
    }

    public UserDTODetails getUserById(long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()->new UserDontExists("Usuário não encontrado"));
        return userDetailsMapper.toDTO(user);
    }

    public boolean userHasEvent(long id){
         UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotFounded("Usuário não encontrado!"));
         if(user.getEventos().isEmpty()){
             return false;
         }
         return true;
    }

    public boolean userHasBusiness(long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotFounded("Usuário não encontrado!"));
        if(user.getBusiness() == null){
            return false;
        }
        return true;
    }

    public Set<UserDTODetails>getUsersPages(int pagina, int itens){
        Set<UserEntity> users = userRepository.findAll(PageRequest.of(pagina, itens)).toSet();
        return users.stream().map(e->userDetailsMapper.toDTO(e)).collect(Collectors.toSet());
    }

    @Transactional
    public UserEditDTO editUserDetails(UserEditDTO dto, long id){
        UserEntity userBefore = userRepository.findById(id).orElseThrow(()->new UserDontExists("Usuário não encontrado"));

        userDetailsMapper.updateEntityFromDtoEdit(dto, userBefore);

        try{
            userRepository.save(userBefore);
            return userDetailsMapper.toDTOEdit(userBefore);
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

    @Transactional
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

    @Transactional
    public void editPasswordByEmail(String email , String novaSenha){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotEdited("Senha não editada"));

        Boolean verificaSenha = passwordEncoder.matches(novaSenha, user.getPassword());

        if(verificaSenha){
            throw new PasswordDoesntMatch("Senhas igual a sua atual. Digite uma nova senha!");
        }

        String hashedPassword = passwordEncoder.encode(novaSenha);

        try{
            user.setPassword(hashedPassword);
            userRepository.save(user);
        } catch (Exception e) {
            throw new UserNotEdited("Senha não alterada");
        }
    }


    @Transactional
    public void deleteUser(long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserDontExists("usuário não encontrado"));
        try{
            userRepository.delete(user);
        } catch (Exception e) {
            throw new UserNotDeleted("Usuário não deletado");
        }
    }

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

    public void verifyEmail(String email){
        UserEntity user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFounded("Email não encontrado"));
    }
}
