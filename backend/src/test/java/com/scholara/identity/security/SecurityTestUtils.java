package com.scholara.identity.security;

import com.scholara.identity.domain.model.User;
import com.scholara.identity.infrastructure.security.ScholaraPrincipal;
import com.scholara.shared.domain.Email;
import com.scholara.shared.domain.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

/**
 * Utility class for security-related test helpers.
 *
 * <p>Provides methods to create authenticated requests in tests
 * when using stateless session management.
 */
public final class SecurityTestUtils {

    private SecurityTestUtils() {
    }

    /**
     * Creates a RequestPostProcessor that authenticates as a user with the given role.
     *
     * <p>Usage:
     * <pre>
     * mockMvc.perform(get("/v1/auth/me")
     *     .with(SecurityTestUtils.scholaraUser(Role.STUDENT)))
     *     .andExpect(status().isOk());
     * </pre>
     *
     * @param role the role for the mock user
     * @return a RequestPostProcessor that sets up authentication
     */
    public static RequestPostProcessor scholaraUser(Role role) {
        return scholaraUser("test@scholara.com", role);
    }

    /**
     * Creates a RequestPostProcessor that authenticates as a user with the given email and role.
     *
     * @param email the email for the mock user
     * @param role the role for the mock user
     * @return a RequestPostProcessor that sets up authentication
     */
    public static RequestPostProcessor scholaraUser(String email, Role role) {
        User user = User.createLocalUser(
                Email.of(email),
                "encoded-password",
                role
        );
        setUserId(user, UUID.randomUUID());
        user.verifyEmail();

        ScholaraPrincipal principal = new ScholaraPrincipal(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        return authentication(auth);
    }

    private static void setUserId(User user, UUID id) {
        try {
            Field idField = user.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set user ID for test", e);
        }
    }
}
