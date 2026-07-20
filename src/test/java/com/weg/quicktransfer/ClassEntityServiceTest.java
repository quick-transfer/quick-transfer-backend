// package com.weg.quicktransfer;

// import com.weg.quicktransfer.dto.classEntity.ClassEntityRequestDTO;
// import com.weg.quicktransfer.dto.classEntity.ClassEntityResponseDTO;
// import com.weg.quicktransfer.mapper.ClassEntityMapper;
// import com.weg.quicktransfer.model.ClassEntity;
// import com.weg.quicktransfer.model.Course;
// import com.weg.quicktransfer.model.Student;
// import com.weg.quicktransfer.repo.ClassEntityRepository;
// import com.weg.quicktransfer.service.ClassEntityService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDate;
// import java.util.ArrayList;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class ClassEntityServiceTest {

//     @Mock
//     private ClassEntityRepository classEntityRepository;

//     @Mock
//     private ClassEntityMapper classEntityMapper;

//     @InjectMocks
//     private ClassEntityService classEntityService;

//     private ClassEntity classEntity;
//     private ClassEntityRequestDTO requestDTO;
//     private ClassEntityResponseDTO responseDTO;
//     private Course course;

//     @BeforeEach
//     void setUp() {
//         course = new Course();
//         course.setId(1L);
//         course.setName("Java");
//         course.setClasses(new ArrayList<>());

//         classEntity = new ClassEntity();
//         classEntity.setId(1L);
//         classEntity.setCourse(course);
//         classEntity.setFinishDate(LocalDate.now());
//         classEntity.setStudents(new ArrayList<Student>());
//         classEntity.setAcronym("JAVA01");

//         requestDTO = new ClassEntityRequestDTO(1L, LocalDate.now(), "JAVA01");
//         responseDTO = new ClassEntityResponseDTO(1L, 1L, LocalDate.now(), "JAVA01");
//     }

//     @Test
//     @DisplayName("Should create class entity and return response dto")
//     void shouldCreateClassEntity() {
//         when(classEntityMapper.toEntity(requestDTO)).thenReturn(classEntity);
//         when(classEntityRepository.save(classEntity)).thenReturn(classEntity);
//         when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

//         ClassEntityResponseDTO result = classEntityService.create(requestDTO);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("JAVA01", result.acronym());

//         verify(classEntityMapper).toEntity(requestDTO);
//         verify(classEntityRepository).save(classEntity);
//         verify(classEntityMapper).toResponse(classEntity);
//     }

//     @Test
//     @DisplayName("Should find class entity by id and return response dto")
//     void shouldFindClassEntityById() {
//         when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//         when(classEntityMapper.toResponse(classEntity)).thenReturn(responseDTO);

//         ClassEntityResponseDTO result = classEntityService.findById(1L);

//         assertNotNull(result);
//         assertEquals(1L, result.id());
//         assertEquals("JAVA01", result.acronym());

//         verify(classEntityRepository).findById(1L);
//         verify(classEntityMapper).toResponse(classEntity);
//     }

//     @Test
//     @DisplayName("Should update class entity and return response dto")
//     void shouldUpdateClassEntity() {
//         // Records são imutáveis, novos valores exigem uma nova instância
//         ClassEntityRequestDTO updatedRequest = new ClassEntityRequestDTO(1L, LocalDate.now().plusDays(30), "JAVA02");

//         ClassEntity updatedEntity = new ClassEntity();
//         updatedEntity.setId(1L);
//         updatedEntity.setCourse(course);
//         updatedEntity.setFinishDate(LocalDate.now().plusDays(30));
//         updatedEntity.setStudents(new ArrayList<Student>());
//         updatedEntity.setAcronym("JAVA02");

//         ClassEntityResponseDTO updatedResponse = new ClassEntityResponseDTO(1L, 1L, LocalDate.now().plusDays(30), "JAVA02");

//         when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
//         when(classEntityMapper.toEntity(updatedRequest)).thenReturn(updatedEntity);
//         when(classEntityRepository.save(any(ClassEntity.class))).thenReturn(updatedEntity);
//         when(classEntityMapper.toResponse(updatedEntity)).thenReturn(updatedResponse);

//         ClassEntityResponseDTO result = classEntityService.update(1L, updatedRequest);

//         assertNotNull(result);
//         assertEquals("JAVA02", result.acronym());

//         verify(classEntityRepository).findById(1L);
//         verify(classEntityMapper).toEntity(updatedRequest);
//         verify(classEntityRepository).save(any(ClassEntity.class));
//         verify(classEntityMapper).toResponse(updatedEntity);
//     }

//     @Test
//     @DisplayName("Should delete class entity")
//     void shouldDeleteClassEntity() {
//         doNothing().when(classEntityRepository).deleteById(1L);

//         assertDoesNotThrow(() -> classEntityService.delete(1L));

//         verify(classEntityRepository).deleteById(1L);
//     }

//     @Test
//     @DisplayName("Should throw exception when request dto is null")
//     void shouldThrowExceptionWhenRequestDtoIsNull() {
//         assertThrows(IllegalArgumentException.class, () -> classEntityService.create(null));
//     }
// }