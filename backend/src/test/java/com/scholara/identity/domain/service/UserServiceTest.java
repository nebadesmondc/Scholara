package com.scholara.identity.domain.service;

import com.scholara.identity.domain.exception.EmailAlreadyExistsException;
import com.scholara.identity.domain.model.User;
import com.scholara.identity.domain.repository.UserRepository;
import com.scholara.shared.domain.AuthProvider;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import com.scholara.shared.event.PasswordChangedEvent;
import com.scholara.shared.event.UserRegisteredEvent;
import com.scholara.shared.event.UserVerifiedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    private Email testEmail;

    @BeforeEach
    void setUp() {
        testEmail = Email.of("test@example.com");
    }

    @Test
    void registerLocalUser_withNewEmail_shouldCreateUser() {
        String password = "Password123";
        Role role = Role.STUDENT;

        when(userRepository.existsByEmail(testEmail.value())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> simulateSave(i.getArgument(0)));

        User result = userService.registerLocalUser(testEmail, password, role);

        assertNotNull(result);
        assertEquals(testEmail.value(), result.getEmail());
        assertEquals(role, result.getRole());
        assertEquals(AuthProvider.LOCAL, result.getProvider());
        assertFalse(result.isEnabled());
        assertFalse(result.isEmailVerified());

        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void registerLocalUser_withExistingEmail_shouldThrowException() {
        when(userRepository.existsByEmail(testEmail.value())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.registerLocalUser(testEmail, "password", Role.STUDENT));

        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void verifyEmail_shouldEnableUser() {
        User user = User.createLocalUser(testEmail, "hashedPassword", Role.STUDENT);
        setUserId(user, UUID.randomUUID()); // Simulate persisted user
        assertFalse(user.isEnabled());
        assertFalse(user.isEmailVerified());

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.verifyEmail(user);

        assertTrue(user.isEmailVerified());
        assertTrue(user.isEnabled());
        verify(userRepository).save(user);

        ArgumentCaptor<UserVerifiedEvent> eventCaptor = ArgumentCaptor.forClass(UserVerifiedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(user.userId(), eventCaptor.getValue().userId());
    }

    @Test
    void changePassword_shouldUpdatePasswordAndPublishEvent() {
        User user = User.createLocalUser(testEmail, "oldHash", Role.STUDENT);
        setUserId(user, UUID.randomUUID()); // Simulate persisted user
        String newPassword = "NewPassword123";

        when(passwordEncoder.encode(newPassword)).thenReturn("newHash");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.changePassword(user, newPassword);

        assertEquals("newHash", user.getPassword());
        verify(userRepository).save(user);

        ArgumentCaptor<PasswordChangedEvent> eventCaptor = ArgumentCaptor.forClass(PasswordChangedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(user.userId(), eventCaptor.getValue().userId());
    }

    @Test
    void findOrCreateOAuthUser_withNewUser_shouldCreateUser() {
        when(userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google123"))
                .thenReturn(Optional.empty());
        when(userRepository.findByEmail(testEmail.value())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> simulateSave(i.getArgument(0)));

        User result = userService.findOrCreateOAuthUser(
                testEmail, AuthProvider.GOOGLE, "google123", Role.STUDENT
        );

        assertNotNull(result);
        assertEquals(testEmail.value(), result.getEmail());
        assertEquals(AuthProvider.GOOGLE, result.getProvider());
        assertEquals("google123", result.getProviderId());
        assertTrue(result.isEnabled());
        assertTrue(result.isEmailVerified());

        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    void findOrCreateOAuthUser_withExistingOAuthUser_shouldReturnExisting() {
        User existingUser = User.createOAuthUser(testEmail, Role.STUDENT, AuthProvider.GOOGLE, "google123");
        when(userRepository.findByProviderAndProviderId(AuthProvider.GOOGLE, "google123"))
                .thenReturn(Optional.of(existingUser));

        User result = userService.findOrCreateOAuthUser(
                testEmail, AuthProvider.GOOGLE, "google123", Role.STUDENT
        );

        assertEquals(existingUser, result);
        verify(userRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void recordLogin_shouldUpdateLastLoginTime() {
        User user = User.createLocalUser(testEmail, "hash", Role.STUDENT);
        assertNull(user.getLastLoginAt());

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.recordLogin(user);

        assertNotNull(user.getLastLoginAt());
        verify(userRepository).save(user);
    }

    /**
     * Simulates JPA save behavior by setting an ID on the user if not already set.
     */
    private User simulateSave(User user) {
        if (user.getId() == null) {
            setUserId(user, UUID.randomUUID());
        }
        return user;
    }

    private void setUserId(User user, UUID id) {
        try {
            Field idField = user.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set user ID for test", e);
        }
    }
}
