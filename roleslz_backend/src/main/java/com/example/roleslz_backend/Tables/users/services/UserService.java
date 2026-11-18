package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.users.DTOS.*;
import com.example.roleslz_backend.Tables.users.DTOS.Req.UserEditDTO;
import com.example.roleslz_backend.Tables.users.entity.AuthProvider;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.mapper.UserRegisterMapperClass;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.*;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.GoogleService;
import com.example.roleslz_backend.infra.service.SecurityService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private final UserUtils userUtils;
    private final ParticipacaoRepository participacaoRepository;

    public UserService(UserRepository userRepository, UserRegisterMapperClass userRegisterMapper, UserDetailsMapperClass userDetailsMapper, PasswordEncoder passwordEncoder, EventoRepository eventoRepository, SecurityService securityService, EmailService emailService, GoogleService googleService, UserUtils userUtils, ParticipacaoRepository participacaoRepository) {
        this.userRepository = userRepository;
        this.userRegisterMapper = userRegisterMapper;
        this.userDetailsMapper = userDetailsMapper;
        this.passwordEncoder = passwordEncoder;
        this.eventoRepository = eventoRepository;
        this.securityService = securityService;
        this.emailService = emailService;
        this.googleService = googleService;
        this.userUtils = userUtils;
        this.participacaoRepository = participacaoRepository;
    }

    // Quantos eventos do histórico o perfil devolve de uma vez (o resto vem por /participacao/historico).
    private static final int HISTORICO_NO_PERFIL = 20;

    private UserEntity getUserLogado() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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

    // Perfil do próprio usuário logado. O id sai do token (userUtils), nunca de parâmetro:
    // não existe caminho aqui para ler o perfil de outra pessoa.
    @Transactional
    public UserProjectionProfile getMeuPerfil(){
        long userId = userUtils.getUserLogado().getId();
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFounded("Usuário não encontrado"));

        List<EventDTOSumary> organizados = user.getEventos() == null ? List.of()
                : user.getEventos().stream()
                    .sorted(Comparator.comparing(EventoEntity::getStartDate,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .map(EventDTOSumary::toSummaryDTO)
                    .toList();

        List<EventDTOSumary> historico = participacaoRepository
                .findEventosParticipadosByUserId(userId, PageRequest.of(0, HISTORICO_NO_PERFIL))
                .getContent().stream()
                .map(EventDTOSumary::toSummaryDTO)
                .toList();

        return new UserProjectionProfile(
                user.getId(),
                user.getNome(),
                user.getSobrenome(),
                user.getSexo(),
                user.getEmail(),
                user.getIdade(),
                organizados,
                historico
        );
    }

    public UserDTODetails getUserById(){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()->new UserDontExists("Usuário não encontrado"));
        return userDetailsMapper.toDTO(user);
    }

    public boolean userHasEvent(){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId())
                .orElseThrow(()-> new UserNotFounded("Usuário não encontrado!"));
        return user.getEventos() != null && !user.getEventos().isEmpty();
    }

    public boolean userHasBusiness(){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId())
                .orElseThrow(()-> new UserNotFounded("Usuário não encontrado!"));
        return user.getBusiness() != null;
    }

    @Transactional
    public UserEditDTO editUserDetails(UserEditDTO dto){
        UserEntity userBefore = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()->new UserDontExists("Usuário não encontrado"));

        userDetailsMapper.updateEntityFromDtoEdit(dto, userBefore);

        try{
            userRepository.save(userBefore);
            return userDetailsMapper.toDTOEdit(userBefore);
        } catch (Exception e) {
            throw new UserNotEdited("Usuário não editado");
        }
    }

    public Boolean verifyPassword(PasswordDTO data){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()-> new UserNotEdited("Senha não editada"));

        Boolean verificaSenha = passwordEncoder.matches(data.password(), user.getPassword());

        if (!verificaSenha){
            throw new PasswordDoesntMatch("Senhas não coincidem!");
        }
        else {
            return true;
        }
    }

    @Transactional
    public void addFirebaseToken(String token){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()->new UserNotFounded("Usuário não encontrado"));
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
    public void editPassword(PasswordDTO data){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()-> new UserNotEdited("Senha não editada"));

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
    public void deleteUser(){
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()-> new UserDontExists("usuário não encontrado"));
        try{
            userRepository.delete(user);
        } catch (Exception e) {
            throw new UserNotDeleted("Usuário não deletado");
        }
    }

    @Transactional
    public void confirmPresence(long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        try{
            // `evento.confirmacoes` é o lado dono da relação. `user.eventos` é
            // @OneToMany(mappedBy="organizador") — mexer nele aqui não persiste nada e
            // ainda suja a lista de eventos que a pessoa ORGANIZA.
            evento.getConfirmacoes().add(user);

            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new PresenceNotConfirmated("Presença não confirmada" + e.getMessage());
        }
    }

    @Transactional
    public void unconfirmPresence( long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));
        UserEntity user = userRepository.findById(userUtils.getUserLogado().getId()).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        try{
            evento.getConfirmacoes().remove(user);

            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new PresenceNotConfirmated("Presença não retirada" + e.getMessage());
        }
    }

}