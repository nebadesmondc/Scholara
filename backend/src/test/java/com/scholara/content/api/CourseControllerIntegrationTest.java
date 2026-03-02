package com.scholara.content.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholara.content.api.dto.CourseRequest;
import com.scholara.content.api.dto.LessonRequest;
import com.scholara.content.api.dto.SubjectRequest;
import com.scholara.content.domain.model.Course;
import com.scholara.content.domain.model.CourseStatus;
import com.scholara.content.domain.repository.CourseRepository;
import com.scholara.content.domain.repository.EnrollmentRepository;
import com.scholara.shared.domain.Role;
import com.scholara.shared.event.CoursePublishedEvent;
import com.scholara.shared.event.StudentEnrolledEvent;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.scholara.identity.security.SecurityTestUtils.scholaraUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private TestEventCollector eventCollector;

    private RequestPostProcessor teacher;
    private RequestPostProcessor student;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestEventCollector testEventCollector() {
            return new TestEventCollector();
        }
    }

    @Getter
    static class TestEventCollector {
        private final List<Object> events = new ArrayList<>();

        @EventListener
        public void handleEvent(Object event) {
            // Only collect our domain events
            if (event instanceof CoursePublishedEvent || event instanceof StudentEnrolledEvent) {
                events.add(event);
            }
        }

        public void clear() {
            events.clear();
        }

        @SuppressWarnings("unchecked")
        public <T> List<T> getEventsOfType(Class<T> type) {
            return events.stream()
                    .filter(type::isInstance)
                    .map(e -> (T) e)
                    .toList();
        }
    }

    @BeforeEach
    void setUp() {
        courseRepository.deleteAll();
        enrollmentRepository.deleteAll();
        eventCollector.clear();
        teacher = scholaraUser("teacher@scholara.com", Role.TEACHER);
        student = scholaraUser("student@scholara.com", Role.STUDENT);
    }

    @Test
    void createCourse_asTeacher_shouldSucceed() throws Exception {
        CourseRequest request = new CourseRequest("Java Programming", "Learn Java from scratch");

        mockMvc.perform(post("/v1/courses")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        assertEquals(1, courseRepository.count());
    }

    @Test
    void createCourse_asStudent_shouldReturn403() throws Exception {
        CourseRequest request = new CourseRequest("Java Programming", "Learn Java from scratch");

        mockMvc.perform(post("/v1/courses")
                        .with(student)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void publishCourse_shouldUpdateStatusAndPublishEvent() throws Exception {
        String responseJson = mockMvc.perform(post("/v1/courses")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseRequest("Java", "Desc"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID courseId = UUID.fromString(objectMapper.readTree(responseJson).get("id").asText());

        mockMvc.perform(patch("/v1/courses/" + courseId + "/publish")
                        .with(teacher)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(CourseStatus.PUBLISHED, updatedCourse.getStatus());

        assertFalse(eventCollector.getEventsOfType(CoursePublishedEvent.class).isEmpty());
    }

    @Test
    void addSubjectAndLesson_shouldWorkCorrectly() throws Exception {
        // 1. Create Course
        String courseResp = mockMvc.perform(post("/v1/courses")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseRequest("Java", "Desc"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID courseId = UUID.fromString(objectMapper.readTree(courseResp).get("id").asText());

        // 2. Add Subject
        String subjectResp = mockMvc.perform(post("/v1/courses/" + courseId + "/subjects")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SubjectRequest("Basics"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID subjectId = UUID.fromString(objectMapper.readTree(subjectResp).get("id").asText());

        // 3. Add Lesson
        mockMvc.perform(post("/v1/courses/" + courseId + "/subjects/" + subjectId + "/lessons")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LessonRequest("Variables", "Content about variables"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Variables"));

        Course finalCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(1, finalCourse.getSubjects().size());
        assertEquals(1, finalCourse.getSubjects().getFirst().getLessons().size());
    }

    @Test
    void enroll_inPublishedCourse_shouldSucceed() throws Exception {
        // 1. Create and Publish Course
        String courseResp = mockMvc.perform(post("/v1/courses")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseRequest("Java", "Desc"))))
                .andReturn().getResponse().getContentAsString();
        JsonNode courseNode = objectMapper.readTree(courseResp);
        UUID courseId = UUID.fromString(courseNode.get("id").asText());

        mockMvc.perform(patch("/v1/courses/" + courseId + "/publish")
                        .with(teacher)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // 2. Enroll as Student
        mockMvc.perform(post("/v1/courses/" + courseId + "/enroll")
                        .with(student)
                        .with(csrf()))
                .andExpect(status().isCreated());

        // 3. Verify enrollment
        assertEquals(1, enrollmentRepository.findAll().size());
        assertFalse(eventCollector.getEventsOfType(StudentEnrolledEvent.class).isEmpty());
    }

    @Test
    void enroll_inDraftCourse_shouldReturn400() throws Exception {
        String courseResp = mockMvc.perform(post("/v1/courses")
                        .with(teacher)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseRequest("Java", "Desc"))))
                .andReturn().getResponse().getContentAsString();
        UUID courseId = UUID.fromString(objectMapper.readTree(courseResp).get("id").asText());

        mockMvc.perform(post("/v1/courses/" + courseId + "/enroll")
                        .with(student)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
