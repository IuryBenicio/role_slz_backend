package com.example.roleslz_backend.tables.Users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import com.example.roleslz_backend.Tables.users.DTOS.UserDTORegister;
import com.example.roleslz_backend.Tables.users.entity.Sexo;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UsersServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void createUser(){

        UserDTORegister userDTO = new UserDTORegister("test", "teste", Sexo.MASCULINO, "teste@gmail.com", "senhaTeste", 18);

        Map<String, String> response = restTemplate.postForObject("/auth/register", userDTO, Map.class);

        assertNotNull(response);

        assertEquals("Usuário registrado", response.get("message"));

        assertNotNull(response.get("token"));
    }
}
