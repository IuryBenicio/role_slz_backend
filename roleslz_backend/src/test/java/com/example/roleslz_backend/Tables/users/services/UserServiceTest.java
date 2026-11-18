package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserExists;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.mapper.UserRegisterMapperClass;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import com.example.roleslz_backend.infra.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRegisterMapperClass userRegisterMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityService securityService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private UserDTORegister validRegisterDTO;
    private UserEntity mockedUser;

    @BeforeEach
    void setUp() {
        validRegisterDTO = new UserDTORegister("Iury", "Benicio", Sexo.MASCULINO, "iury@test.com", "senha123", 25);

        mockedUser = new UserEntity();
        mockedUser.setEmail("iury@test.com");
        mockedUser.setEnable(false);
    }

    @Test
    void addNewUserService_DeveCriarUsuarioComSucesso() {
        // Arrange: Dizemos ao mock o que ele deve responder
        when(userRepository.findByEmail(validRegisterDTO.email())).thenReturn(Optional.empty());
        when(userRegisterMapper.toEntity(validRegisterDTO)).thenReturn(mockedUser);
        when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
        when(securityService.generateToken(any())).thenReturn("fake-jwt-token");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockedUser);

        // Act: Rodamos o método
        UserEntity result = userService.addNewUserService(validRegisterDTO);

        // Assert: Verificamos se deu tudo certo
        assertNotNull(result);
        assertEquals("iury@test.com", result.getEmail());
        assertFalse(result.isEnabled()); // Precisa estar false até validar email
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString());
    }

    @Test
    void addNewUserService_DeveLancarExceptionSeEmailJaExistir() {
        // Arrange: Simulamos que o e-mail já existe no banco
        when(userRepository.findByEmail(validRegisterDTO.email())).thenReturn(Optional.of(mockedUser));

        // Act & Assert
        Exception exception = assertThrows(UserExists.class, () -> {
            userService.addNewUserService(validRegisterDTO);
        });

        assertEquals("Este email já está vinculado a um usuário!", exception.getMessage());
        verify(userRepository, never()).save(any()); // Garante que não salvou no banco
    }
}