package com.example.usermgmt.infrastructure.adapter.in.rest;

import com.example.usermgmt.domain.port.UserServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

class UserRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserServicePort userServicePort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserRestController userRestController = new UserRestController(userServicePort);
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController).build();
    }

    @Test
    void generateUserReport_ShouldReturnExcelFileWithTimestamp() throws Exception {
        // Arrange
        byte[] mockReport = new byte[]{1, 2, 3};
        when(userServicePort.generateUserReport()).thenReturn(mockReport);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/report"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment; filename=users_report_")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString(".xlsx")))
                .andExpect(content().contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")))
                .andExpect(content().bytes(mockReport));
    }
}
