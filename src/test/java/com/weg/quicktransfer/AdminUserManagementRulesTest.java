package com.weg.quicktransfer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.weg.quicktransfer.model.*;
import com.weg.quicktransfer.repository.UserRepository;
import com.weg.quicktransfer.exception.BusinessRuleException;
import com.weg.quicktransfer.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AdminUserManagementRulesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("The saved user should be an instance of Manager")
    public void adminRuleCreateManagerValidDataShouldSaveSuccessfully() {
        Manager newManager = new Manager();
        newManager.setUsername("manager.john");
        newManager.setEmail("john@weg.net");
        newManager.setPassword("securePass123");

        when(userRepository.findByUsername("manager.john")).thenReturn(Optional.empty());
        when(userRepository.save(any(Manager.class))).thenReturn(newManager);

        User savedUser = adminService.createUser(newManager);

        assertNotNull(savedUser);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertTrue(capturedUser instanceof Manager);
        assertEquals("manager.john", capturedUser.getUsername());
    }

    @Test
    @DisplayName("Admin should not be able to create a user with an already existing username")
    public void adminRuleCreateStudentDuplicatedUsernameShouldThrowException() {
        Student newStudent = new Student();
        newStudent.setUsername("student.maria");

        User existingUser = new Manager();
        existingUser.setUsername("student.maria");

        when(userRepository.findByUsername("student.maria")).thenReturn(Optional.of(existingUser));

        assertThrows(BusinessRuleException.class, () -> {
            adminService.createUser(newStudent);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Username should remain the same")
    public void adminRuleUpdateUserShouldUpdateOnlyAllowedFields() {
        Long userId = 1L;
        Coordinator existingCoordinator = new Coordinator();
        existingCoordinator.setId(userId);
        existingCoordinator.setUsername("coord.peter");
        existingCoordinator.setName("Peter Old Name");

        Coordinator updateData = new Coordinator();
        updateData.setName("Peter New Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingCoordinator));
        when(userRepository.save(any(Coordinator.class))).thenReturn(existingCoordinator);

        User updatedUser = adminService.updateUser(userId, updateData);

        assertEquals("Peter New Name", updatedUser.getName());
        assertEquals("coord.peter", updatedUser.getUsername());
        verify(userRepository).save(existingCoordinator);
    }

    @Test
    public void adminRuleDeleteUserExistingUserShouldDeleteSuccessfully() {
        Long userId = 99L;
        User existingUser = new Student();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        adminService.deleteUser(userId);

        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    @DisplayName("Should throw an exception when admin tries to delete a non-existing user")
    public void adminRuleDeleteUserNonExistingUserShouldThrowException() {
        Long invalidUserId = 999L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(BusinessRuleException.class, () -> {
            adminService.deleteUser(invalidUserId);
        });

        verify(userRepository, never()).delete(any());
    }
}