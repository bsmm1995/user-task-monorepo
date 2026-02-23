package com.example.usermgmt.application.usecase;

import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.domain.port.TaskExternalServicePort;
import com.example.usermgmt.domain.port.UserRepositoryPort;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TaskExternalServicePort taskExternalServicePort;

    @InjectMocks
    private UserServiceUseCase userServiceUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setEmail("john.doe@example.com");
    }

    @Test
    void generateUserReport_ShouldReturnValidExcelBytes() throws IOException {
        // Arrange
        when(userRepositoryPort.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(sampleUser)));
        
        TaskExternalServicePort.TaskExternalDto taskDto = new TaskExternalServicePort.TaskExternalDto(
                101L, "Task 1", "Description 1", "PENDING", 1L, OffsetDateTime.now()
        );
        
        when(taskExternalServicePort.getTasksByUserId(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(taskDto));

        // Act
        byte[] reportBytes = userServiceUseCase.generateUserReport();

        // Assert
        assertNotNull(reportBytes);
        assertTrue(reportBytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(reportBytes))) {
            assertEquals(1, workbook.getNumberOfSheets());
            assertEquals("Users and Tasks", workbook.getSheetName(0));
            var sheet = workbook.getSheetAt(0);
            
            // Check title
            assertEquals("REPORTE GENERAL DE USUARIOS Y TAREAS", sheet.getRow(0).getCell(0).getStringCellValue());
            
            // Check user data presence
            boolean userFound = false;
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null && 
                    row.getCell(0).getCellType() == org.apache.poi.ss.usermodel.CellType.STRING &&
                    row.getCell(0).getStringCellValue().contains("John Doe")) {
                    userFound = true;
                    break;
                }
            }
            assertTrue(userFound, "User John Doe should be in the report");
        }
    }

    @Test
    void generateUserReport_WhenNoTasks_ShouldStillGenerateReport() throws IOException {
        // Arrange
        when(userRepositoryPort.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(sampleUser)));
        
        when(taskExternalServicePort.getTasksByUserId(eq(1L), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // Act
        byte[] reportBytes = userServiceUseCase.generateUserReport();

        // Assert
        assertNotNull(reportBytes);
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(reportBytes))) {
            var sheet = workbook.getSheetAt(0);
            boolean emptyMessageFound = false;
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                var row = sheet.getRow(i);
                if (row != null && row.getCell(0) != null && 
                    row.getCell(0).getCellType() == org.apache.poi.ss.usermodel.CellType.STRING &&
                    row.getCell(0).getStringCellValue().contains("No se encontraron tareas")) {
                    emptyMessageFound = true;
                    break;
                }
            }
            assertTrue(emptyMessageFound, "Empty tasks message should be in the report");
        }
    }
}
