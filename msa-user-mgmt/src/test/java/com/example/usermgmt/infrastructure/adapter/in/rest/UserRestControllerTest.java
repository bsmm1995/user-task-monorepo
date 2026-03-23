package com.example.usermgmt.infrastructure.adapter.in.rest;

import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApiController;
import com.example.usermgmt.infrastructure.adapter.in.rest.api.UserManagementApiDelegate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

class UserRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserManagementApiDelegate userManagementApiDelegate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserManagementApiController userManagementApiController = new UserManagementApiController(userManagementApiDelegate);
        mockMvc = MockMvcBuilders.standaloneSetup(userManagementApiController).build();
    }

    @Test
    void generateUserReport_ShouldReturnExcelFileWithTimestamp() throws Exception {
        // Arrange
        byte[] mockReport = new byte[]{1, 2, 3};
        ByteArrayResource resource = new ByteArrayResource(mockReport);
        String filename = "users_report_20230101_120000.xlsx";

        when(userManagementApiDelegate.generateUserReport()).thenReturn(
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .body(resource)
        );

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/report"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment; filename=users_report_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(".xlsx")))
                .andExpect(content().contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(content().bytes(mockReport));
    }
}
