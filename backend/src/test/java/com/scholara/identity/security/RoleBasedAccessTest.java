package com.scholara.identity.security;

import com.scholara.shared.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.scholara.identity.security.SecurityTestUtils.scholaraUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleBasedAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedAccess_toProtectedEndpoint_shouldReturn401() throws Exception {
        mockMvc.perform(get("/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentAccess_toMeEndpoint_shouldSucceed() throws Exception {
        mockMvc.perform(get("/v1/auth/me")
                        .with(scholaraUser(Role.STUDENT)))
                .andExpect(status().isOk());
    }

    @Test
    void studentAccess_toAdminEndpoint_shouldReturn403() throws Exception {
        mockMvc.perform(get("/v1/admin/users")
                        .with(scholaraUser(Role.STUDENT)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminAccess_toAdminEndpoint_shouldSucceed() throws Exception {
        // Returns 404 since endpoint doesn't exist yet, but won't be 403
        mockMvc.perform(get("/v1/admin/users")
                        .with(scholaraUser(Role.ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void studentAccess_toTeacherEndpoint_shouldReturn403() throws Exception {
        mockMvc.perform(get("/v1/teacher/courses")
                        .with(scholaraUser(Role.STUDENT)))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherAccess_toTeacherEndpoint_shouldSucceed() throws Exception {
        // Returns 404 since endpoint doesn't exist yet, but won't be 403
        mockMvc.perform(get("/v1/teacher/courses")
                        .with(scholaraUser(Role.TEACHER)))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminAccess_toTeacherEndpoint_shouldSucceed() throws Exception {
        // Admin should have access to teacher endpoints
        mockMvc.perform(get("/v1/teacher/courses")
                        .with(scholaraUser(Role.ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void publicEndpoint_health_shouldBeAccessible() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpoint_register_shouldBeAccessible() throws Exception {
        // POST without body will fail validation, but won't be 401
        mockMvc.perform(get("/v1/auth/register"))
                .andExpect(status().isMethodNotAllowed()); // GET not allowed, but not 401
    }
}
