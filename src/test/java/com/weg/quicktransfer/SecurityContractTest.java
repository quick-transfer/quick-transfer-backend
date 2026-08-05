package com.weg.quicktransfer;

import com.weg.quicktransfer.config.SecurityConfig;
import com.weg.quicktransfer.controller.AuthController;
import com.weg.quicktransfer.controller.CoordinatorController;
import com.weg.quicktransfer.controller.ManagerController;
import com.weg.quicktransfer.controller.UserController;
import com.weg.quicktransfer.controller.VacancySkillController;
import com.weg.quicktransfer.dto.auth.PasswordResetRequestDTO;
import com.weg.quicktransfer.dto.coordinator.CoordinatorUpdateRequestDTO;
import com.weg.quicktransfer.dto.manager.ManagerUpdateRequestDTO;
import com.weg.quicktransfer.dto.user.UserUpdateRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillRequestDTO;
import com.weg.quicktransfer.dto.vacancy.VacancySkillUpdateRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityContractTest {

    @Test
    void shouldEnableMethodSecurity() {
        assertTrue(SecurityConfig.class.isAnnotationPresent(EnableMethodSecurity.class));
    }

    @Test
    void shouldAuthorizeProfileUpdatesUsingAuthenticatedPrincipal() throws Exception {
        assertPrincipalOwnership(ManagerController.class, "updateManager", ManagerUpdateRequestDTO.class);
        assertPrincipalOwnership(CoordinatorController.class, "updateCoordinator", CoordinatorUpdateRequestDTO.class);
        assertPrincipalOwnership(UserController.class, "updateUser", UserUpdateRequestDTO.class);
    }

    @Test
    void shouldNotAcceptEmailRecipientFromRequest() throws Exception {
        Method method = ManagerController.class.getMethod(
                "postSendInterviewEmail", UUID.class, Authentication.class);
        assertEquals(2, method.getParameterCount());
        assertFalse(Arrays.stream(method.getParameters())
                .anyMatch(parameter -> parameter.isAnnotationPresent(RequestParam.class)));
    }

    @Test
    void shouldResetOnlyAuthenticatedUsersPassword() throws Exception {
        Method method = AuthController.class.getMethod(
                "resetPassword", PasswordResetRequestDTO.class, Authentication.class);
        assertNotNull(method.getAnnotation(PreAuthorize.class));
    }

    @Test
    void shouldAuthorizeVacancySkillOperationsByRole() throws Exception {
        assertAuthorization(
                VacancySkillController.class.getMethod("create", VacancySkillRequestDTO.class),
                "hasRole('MANAGER')");
        assertAuthorization(
                VacancySkillController.class.getMethod("findById", UUID.class),
                "hasAnyRole('COORDINATOR', 'MANAGER')");
        assertAuthorization(
                VacancySkillController.class.getMethod(
                        "update", UUID.class, VacancySkillUpdateRequestDTO.class),
                "hasRole('MANAGER')");
        assertAuthorization(
                VacancySkillController.class.getMethod("delete", UUID.class),
                "hasRole('MANAGER')");
    }

    private void assertPrincipalOwnership(
            Class<?> controller,
            String methodName,
            Class<?> requestType) throws Exception {
        Method method = controller.getMethod(
                methodName, UUID.class, requestType, Authentication.class);
        PreAuthorize authorization = method.getAnnotation(PreAuthorize.class);

        assertNotNull(authorization);
        assertTrue(authorization.value().contains("authentication.principal.id"));
        assertFalse(Arrays.stream(method.getParameters())
                .anyMatch(parameter -> parameter.isAnnotationPresent(RequestParam.class)));
    }

    private void assertAuthorization(Method method, String expectedExpression) {
        PreAuthorize authorization = method.getAnnotation(PreAuthorize.class);

        assertNotNull(authorization);
        assertEquals(expectedExpression, authorization.value());
    }
}
