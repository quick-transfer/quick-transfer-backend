package com.weg.quicktransfer;

import com.weg.quicktransfer.model.Coordinator;
import com.weg.quicktransfer.model.Manager;
import com.weg.quicktransfer.model.Student;
import com.weg.quicktransfer.model.User;
import com.weg.quicktransfer.repo.UserRepository;
import com.weg.quicktransfer.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserManagementRulesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("The saved user should be an instance of Manager")
    void adminRuleCreateManagerValidDataShouldSaveSuccessfully() {
        Manager newManager = new Manager();
        setField(newManager, "username", "manager.john");
        setField(newManager, "email", "john@weg.net");
        setField(newManager, "password", "securePass123");

        when(userRepository.findByUserName("manager.john")).thenReturn(Optional.empty());
        when(userRepository.save(any(Manager.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = adminService.createUser(newManager);

        assertNotNull(savedUser);
        assertInstanceOf(Manager.class, savedUser);
        assertEquals("manager.john", readStringField(savedUser, "username", "userName"));

        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertInstanceOf(Manager.class, capturedUser);
        assertEquals("manager.john", readStringField(capturedUser, "username", "userName"));
        assertEquals("john@weg.net", readStringField(capturedUser, "email"));
        assertEquals("securePass123", readStringField(capturedUser, "password"));
    }

    @Test
    @DisplayName("Admin should not be able to create a user with an already existing username")
    void adminRuleCreateStudentDuplicatedUsernameShouldThrowException() {
        Student newStudent = new Student();
        setField(newStudent, "username", "student.maria");

        User existingUser = new Manager();
        setField(existingUser, "username", "student.maria");

        when(userRepository.findByUserName("student.maria")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> adminService.createUser(newStudent));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Username should remain the same")
    void adminRuleUpdateUserShouldUpdateOnlyAllowedFields() {
        Long userId = 1L;

        Coordinator existingCoordinator = new Coordinator();
        setField(existingCoordinator, "id", userId);
        setField(existingCoordinator, "username", "coord.peter");
        setField(existingCoordinator, "name", "Peter Old Name");

        Coordinator updateData = new Coordinator();
        setField(updateData, "name", "Peter New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingCoordinator));
        when(userRepository.save(any(Coordinator.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = adminService.updateUser(userId, updateData);

        assertNotNull(updatedUser);
        assertInstanceOf(Coordinator.class, updatedUser);
        assertEquals("Peter New Name", readStringField(updatedUser, "name"));
        assertEquals("coord.peter", readStringField(updatedUser, "username", "userName"));

        verify(userRepository).save(userCaptor.capture());

        Coordinator savedCoordinator = (Coordinator) userCaptor.getValue();
        assertEquals("Peter New Name", readStringField(savedCoordinator, "name"));
        assertEquals("coord.peter", readStringField(savedCoordinator, "username", "userName"));
    }

    @Test
    @DisplayName("Admin should delete an existing user successfully")
    void adminRuleDeleteUserExistingUserShouldDeleteSuccessfully() {
        Long userId = 99L;

        User existingUser = new Manager();
        setField(existingUser, "id", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> adminService.deleteUser(userId));

        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    @DisplayName("Should throw an exception when admin tries to delete a non-existing user")
    void adminRuleDeleteUserNonExistingUserShouldThrowException() {
        Long invalidUserId = 999L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> adminService.deleteUser(invalidUserId));

        verify(userRepository, never()).delete(any());
    }

    private static void setField(Object target, String fieldName, Object value) {
        Field field = findField(target.getClass(), fieldName);
        if (field == null) {
            return;
        }
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not set field '" + fieldName + "' on " + target.getClass().getSimpleName(), e);
        }
    }

    private static String readStringField(Object target, String... fieldNames) {
        for (String fieldName : fieldNames) {
            Field field = findField(target.getClass(), fieldName);
            if (field == null) {
                continue;
            }
            try {
                field.setAccessible(true);
                Object value = field.get(target);
                if (value != null) {
                    return String.valueOf(value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not read field '" + fieldName + "' from " + target.getClass().getSimpleName(), e);
            }
        }
        return null;
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}