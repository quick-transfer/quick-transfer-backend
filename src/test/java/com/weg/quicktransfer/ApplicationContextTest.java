package com.weg.quicktransfer;

import com.weg.quicktransfer.controller.AdminController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    private static final KeyPair KEY_PAIR = generateKeyPair();

    @Autowired
    private AdminController adminController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    void generatedSchemaMatchesVersionedMigrationColumns() {
        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("USERS", Set.of("ID", "NAME", "USER_NAME", "EMAIL", "PASSWORD",
                        "ROLE", "FIRST_LOGIN", "TOKEN_VERSION", "VERSION")),
                Map.entry("ADMINS", Set.of("USER_ID")),
                Map.entry("COORDINATORS", Set.of("USER_ID")),
                Map.entry("MANAGERS", Set.of("USER_ID", "SECTION")),
                Map.entry("COURSES", Set.of("ID", "NAME", "COORDINATOR_ID", "VERSION")),
                Map.entry("CLASS_ENTITIES", Set.of("ID", "COURSE_ID", "START_DATE", "FINISH_DATE",
                        "STATUS", "SHIFT_CLASS", "ACRONYM", "VERSION")),
                Map.entry("STUDENTS", Set.of("ID", "NAME", "EMAIL", "AGE", "AVERAGE_GRADE",
                        "STATUS", "HAS_SEEN_EMAIL", "STATUS_STUDENT", "CLASSENTITY_ID", "VERSION")),
                Map.entry("SKILLS", Set.of("ID", "NAME", "SKILL_TYPE", "GRADE", "STUDENT_ID", "VERSION")),
                Map.entry("PLACES", Set.of("ID", "PLACE_NAME", "PARK", "SECTION", "VERSION")),
                Map.entry("VACANCIES", Set.of("ID", "NAME", "DESCRIPTION", "NUMBERS_VACANCIES",
                        "AREA", "SHIFT", "PLACE_ID", "VERSION")),
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
        assertThrows(AccessDeniedException.class, adminController::findAllAdmins);
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
