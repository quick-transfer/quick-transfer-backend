// package com.weg.quicktransfer;

// import com.weg.quicktransfer.dto.admin.AdminRequestDTO;
// import com.weg.quicktransfer.dto.admin.AdminResponseDTO;
// import com.weg.quicktransfer.enums.Role;
// import com.weg.quicktransfer.mapper.AdminMapper;
// import com.weg.quicktransfer.model.Admin;
// import com.weg.quicktransfer.repo.AdminRepository;
// import com.weg.quicktransfer.service.AdminService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class AdminServiceTest {

//     @Mock
//     private AdminRepository adminRepo;

//     @Mock
//     private AdminMapper adminMapper;

//     @InjectMocks
//     private AdminService adminService;

//     private Admin admin;
//     private AdminRequestDTO requestDTO;
//     private AdminResponseDTO responseDTO;

//     @BeforeEach
//     void setUp() {

//         admin = Admin.builder()
//                 .id(1L)
//                 .name("Administrador")
//                 .userName("admin01")
//                 .email("admin@weg.com")
//                 .password("123456")
//                 .role(Role.ADMIN)
//                 .build();

//         requestDTO = new AdminRequestDTO(
//                 "Administrador",
//                 "admin01",
//                 "admin@weg.com",
//                 "123456"
//         );

//         responseDTO = new AdminResponseDTO(
//                 1L,
//                 "Administrador",
//                 "admin01",
//                 "admin@weg.com",
//                 Role.ADMIN
//         );
//     }

//     @Test
//     @DisplayName("Should create admin and return response dto")
//     void shouldCreateAdmin() {

//         when(adminMapper.toEntity(requestDTO))
//                 .thenReturn(admin);

//         when(adminRepo.save(admin))
//                 .thenReturn(admin);

//         when(adminMapper.toResponse(admin))
//                 .thenReturn(responseDTO);

//         AdminResponseDTO result = adminService.create(requestDTO);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Administrador", result.name());
//         assertEquals("admin01", result.userName());
//         assertEquals("admin@weg.com", result.email());
//         assertEquals(Role.ADMIN, result.role());

//         verify(adminMapper).toEntity(requestDTO);
//         verify(adminRepo).save(admin);
//         verify(adminMapper).toResponse(admin);
//     }

//     @Test
//     @DisplayName("Should throw exception when request dto is null")
//     void shouldThrowExceptionWhenRequestDtoIsNull() {

//         assertThrows(
//                 IllegalArgumentException.class,
//                 () -> adminService.create(null)
//         );

//         verify(adminRepo, never()).save(any());
//     }

//     @Test
//     @DisplayName("Should find admin by id and return response dto")
//     void shouldFindAdminById() {

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.of(admin));

//         when(adminMapper.toResponse(admin))
//                 .thenReturn(responseDTO);

//         AdminResponseDTO result = adminService.findById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Administrador", result.name());
//         assertEquals("admin01", result.userName());
//         assertEquals("admin@weg.com", result.email());
//         assertEquals(Role.ADMIN, result.role());

//         verify(adminRepo).findById(1L);
//         verify(adminMapper).toResponse(admin);
//     }

//     @Test
//     @DisplayName("Should throw exception when admin is not found by id")
//     void shouldThrowExceptionWhenAdminNotFound() {

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.empty());

//         assertThrows(
//                 RuntimeException.class,
//                 () -> adminService.findById(1L)
//         );

//         verify(adminMapper, never()).toResponse(any());
//     }

//     @Test
//     @DisplayName("Should update admin and return response dto")
//     void shouldUpdateAdmin() {

//         AdminRequestDTO updatedRequest = new AdminRequestDTO(
//                 "Administrador Atualizado",
//                 "admin02",
//                 "admin2@weg.com",
//                 "654321"
//         );

//         Admin updatedAdmin = Admin.builder()
//                 .id(1L)
//                 .name("Administrador Atualizado")
//                 .userName("admin02")
//                 .email("admin2@weg.com")
//                 .password("654321")
//                 .role(Role.ADMIN)
//                 .build();

//         AdminResponseDTO updatedResponse = new AdminResponseDTO(
//                 1L,
//                 "Administrador Atualizado",
//                 "admin02",
//                 "admin2@weg.com",
//                 Role.ADMIN
//         );

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.of(admin));

//         when(adminRepo.save(any(Admin.class)))
//                 .thenReturn(updatedAdmin);

//         when(adminMapper.toResponse(any(Admin.class)))
//                 .thenReturn(updatedResponse);

//         AdminResponseDTO result =
//                 adminService.update(1L, updatedRequest);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("Administrador Atualizado", result.name());
//         assertEquals("admin02", result.userName());
//         assertEquals("admin2@weg.com", result.email());

//         ArgumentCaptor<Admin> captor =
//                 ArgumentCaptor.forClass(Admin.class);

//         verify(adminRepo).save(captor.capture());

//         Admin savedAdmin = captor.getValue();

//         assertEquals(
//                 "Administrador Atualizado",
//                 savedAdmin.getName()
//         );

//         assertEquals(
//                 "admin02",
//                 savedAdmin.getUserName()
//         );

//         assertEquals(
//                 "admin2@weg.com",
//                 savedAdmin.getEmail()
//         );

//         assertEquals(
//                 "654321",
//                 savedAdmin.getPassword()
//         );

//         verify(adminRepo).findById(1L);
//         verify(adminMapper).toResponse(any(Admin.class));
//     }

//     @Test
//     @DisplayName("Should throw exception when updating non-existent admin")
//     void shouldThrowExceptionWhenUpdatingNonExistentAdmin() {

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.empty());

//         assertThrows(
//                 RuntimeException.class,
//                 () -> adminService.update(1L, requestDTO)
//         );

//         verify(adminRepo, never()).save(any());
//     }

//     @Test
//     @DisplayName("Should delete admin")
//     void shouldDeleteAdmin() {

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.of(admin));

//         doNothing()
//                 .when(adminRepo)
//                 .deleteById(1L);

//         assertDoesNotThrow(
//                 () -> adminService.delete(1L)
//         );

//         verify(adminRepo).findById(1L);
//         verify(adminRepo).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Should throw exception when deleting non-existent admin")
//     void shouldThrowExceptionWhenDeletingNonExistentAdmin() {

//         when(adminRepo.findById(1L))
//                 .thenReturn(Optional.empty());

//         assertThrows(
//                 RuntimeException.class,
//                 () -> adminService.delete(1L)
//         );

//         verify(adminRepo, never()).deleteById(anyLong());
//     }
// }