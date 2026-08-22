package com.peluqueria.backend;

import com.peluqueria.backend.users.dtos.LoginRequest;
import com.peluqueria.backend.users.dtos.RegisterRequest;
import com.peluqueria.backend.users.repositories.UserAccountRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String testEmail;

    @BeforeEach
    void setUp() {
        // A real MySQL database can contain users referenced by workers or appointments.
        // Each test uses its own email instead of deleting shared data.
        testEmail = "auth-test-" + UUID.randomUUID() + "@example.com";
    }

    @AfterEach
    void tearDown() {
        // Only remove the user created by this test. It is a CLIENT and has no child entities.
        userRepository.findByEmail(testEmail).ifPresent(userRepository::delete);
    }

    @Test
    void testRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest(
                testEmail,
                "securePassword",
                "Juan",
                "Pérez",
                "600123456"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is(testEmail)))
                .andExpect(jsonPath("$.nombre", is("Juan")))
                .andExpect(jsonPath("$.role", is("CLIENT")))
                .andExpect(jsonPath("$.activo", is(true)));
    }

    @Test
    void testLoginSuccessfully() throws Exception {
        // First register a user
        RegisterRequest registerRequest = new RegisterRequest(
                testEmail,
                "password123",
                "Maria",
                "Gomez",
                "611223344"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Attempt login
        LoginRequest loginRequest = new LoginRequest(testEmail, "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.email", is(testEmail)))
                .andExpect(jsonPath("$.role", is("CLIENT")));
    }

    @Test
    void testLoginWithWrongCredentialsFails() throws Exception {
        LoginRequest loginRequest = new LoginRequest(testEmail, "wrongPassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", notNullValue()));
    }
}
