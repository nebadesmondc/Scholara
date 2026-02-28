package com.scholara.identity.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholara.identity.api.dto.LoginRequest;
import com.scholara.identity.api.dto.RegisterRequest;
import com.scholara.identity.api.dto.VerifyEmailRequest;
import com.scholara.identity.domain.model.EmailVerificationToken;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.EmailVerificationTokenRepository;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    void register_withValidData_shouldCreateUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "newuser@example.com",
                "Password123",
                Role.STUDENT
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.emailVerified").value(false));

        assertTrue(userRepository.existsByEmail("newuser@example.com"));
    }

    @Test
    void register_withExistingEmail_shouldReturn409() throws Exception {
        createVerifiedUser("existing@example.com");

        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "Password123",
                Role.STUDENT
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("REG_001"));
    }

    @Test
    void register_withInvalidEmail_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "not-an-email",
                "Password123",
                Role.STUDENT
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VAL_001"));
    }

    @Test
    void register_withWeakPassword_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "weak",
                Role.STUDENT
        );

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VAL_001"));
    }

    @Test
    void login_withValidCredentials_shouldSetCookies() throws Exception {
        createVerifiedUser("user@example.com");

        LoginRequest request = new LoginRequest("user@example.com", "Password123");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andExpect(cookie().httpOnly("access_token", true))
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void login_withInvalidCredentials_shouldReturn401() throws Exception {
        createVerifiedUser("user@example.com");

        LoginRequest request = new LoginRequest("user@example.com", "WrongPassword");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_001"));
    }

    @Test
    void login_withUnverifiedAccount_shouldReturn403() throws Exception {
        createUnverifiedUser("unverified@example.com");

        LoginRequest request = new LoginRequest("unverified@example.com", "Password123");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("AUTH_002"));
    }

    @Test
    void verifyEmail_withValidOtp_shouldVerifyUser() throws Exception {
        User user = createUnverifiedUser("verify@example.com");
        EmailVerificationToken token = EmailVerificationToken.create(user.getId());
        tokenRepository.save(token);

        VerifyEmailRequest request = new VerifyEmailRequest(user.getId(), token.getOtpCode());

        mockMvc.perform(post("/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        User verifiedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(verifiedUser.isEmailVerified());
        assertTrue(verifiedUser.isEnabled());
    }

    @Test
    void verifyEmail_withInvalidOtp_shouldReturn400() throws Exception {
        User user = createUnverifiedUser("verify@example.com");

        VerifyEmailRequest request = new VerifyEmailRequest(user.getId(), "000000");

        mockMvc.perform(post("/v1/auth/verify-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REG_002"));
    }

    @Test
    void me_whenNotAuthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldClearCookies() throws Exception {
        mockMvc.perform(post("/v1/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge("refresh_token", 0));
    }

    private void createVerifiedUser(String email) {
        User user = User.createLocalUser(
                Email.of(email),
                passwordEncoder.encode("Password123"),
                Role.STUDENT
        );
        user.verifyEmail();
        userRepository.save(user);
    }

    private User createUnverifiedUser(String email) {
        User user = User.createLocalUser(
                Email.of(email),
                passwordEncoder.encode("Password123"),
                Role.STUDENT
        );
        return userRepository.save(user);
    }
}
