package com.weg.quicktransfer;

import com.weg.quicktransfer.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.context.WebApplicationContext;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    private static final KeyPair KEY_PAIR = generateKeyPair();

    @Autowired
    private AdminController adminController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private WebApplicationContext applicationContext;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("app.security.jwt.public-key",
                () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPublic().getEncoded()));
        registry.add("app.security.jwt.private-key",
                () -> Base64.getEncoder().encodeToString(KEY_PAIR.getPrivate().getEncoded()));
        registry.add("app.cors.allowed-origins", () -> "http://localhost:3000");
        registry.add("spring.mail.username", () -> "test@example.com");
        registry.add("spring.mail.password", () -> "test-password");
        registry.add("app.mail.from", () -> "test@example.com");
        registry.add("app.frontend-url", () -> "http://localhost:3000");
    }

    @Test
    void contextLoads() {
    }

    @Test
    void swaggerRequestsBasicAuthenticationFromTheBrowser() throws Exception {
        webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build()
                .perform(get("/swagger-ui/index.html"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(
                        HttpHeaders.WWW_AUTHENTICATE,
                        containsString("Basic realm=\"QuickTransferAPI\"")));
    }

    @Test
    void generatedSchemaMatchesVersionedMigrationColumns() {
        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("USERS", Set.of("ID", "NAME", "USER_NAME", "EMAIL", "PASSWORD",
                        "ROLE", "FIRST_LOGIN", "TOKEN_VERSION", "ACTIVE", "VERSION")),
                Map.entry("ADMINS", Set.of("USER_ID")),
                Map.entry("COORDINATORS", Set.of("USER_ID")),
                Map.entry("MANAGERS", Set.of("USER_ID", "SECTION")),
                Map.entry("COURSES", Set.of("ID", "NAME", "CODE", "STATUS", "COORDINATOR_ID", "VERSION")),
                Map.entry("CLASS_ENTITIES", Set.of("ID", "COURSE_ID", "START_DATE", "FINISH_DATE",
                        "STATUS", "SHIFT_CLASS", "ACRONYM", "NAME", "MAX_STUDENTS", "VERSION")),
                Map.entry("STUDENTS", Set.of("ID", "NAME", "EMAIL", "AGE", "AVERAGE_GRADE",
                        "STATUS", "HAS_SEEN_EMAIL", "STATUS_STUDENT", "REGISTRATION",
                        "ATTENDANCE_RATE", "CLASSENTITY_ID", "VERSION")),
                Map.entry("SKILLS", Set.of("ID", "NAME", "SKILL_TYPE", "GRADE", "STUDENT_ID", "VERSION")),
                Map.entry("PLACES", Set.of("ID", "PLACE_NAME", "CODE", "DESCRIPTION", "CITY",
                        "STATE", "STATUS", "PARK", "SECTION", "VERSION")),
                Map.entry("VACANCIES", Set.of("ID", "NAME", "DESCRIPTION", "NUMBERS_VACANCIES",
                        "AREA", "SHIFT", "STATUS", "PLACE_ID", "MANAGER_ID", "VERSION")),
                Map.entry("VACANCY_SKILLS", Set.of("ID", "NAME", "SKILL_TYPE", "MINIMUM_GRADE", "VERSION")),
                Map.entry("VACANCY_SKILL_ASSIGNMENTS", Set.of("VACANCY_ID", "VACANCY_SKILL_ID")),
                Map.entry("INTERVIEWS", Set.of("ID", "INTERVIEWER_NAME", "DATE_TIME", "VACANCY_ID",
                        "PLACE_ID", "MANAGER_ID", "STUDENT_ID", "REMINDER_SENT", "VERSION")));

        expected.forEach((table, expectedColumns) -> {
            Set<String> actualColumns = Set.copyOf(jdbcTemplate.queryForList(
                    "select column_name from information_schema.columns "
                            + "where table_schema = 'PUBLIC' and table_name = ?",
                    String.class,
                    table));
            org.junit.jupiter.api.Assertions.assertEquals(expectedColumns, actualColumns, table);
        });
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void methodSecurityRejectsInsufficientRole() {
        assertThrows(AccessDeniedException.class, () -> adminController.findAllAdmins(Pageable.unpaged()));
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate test RSA keys", ex);
        }
    }
}
