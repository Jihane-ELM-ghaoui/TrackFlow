package ma.ensa.StorageManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensa.StorageManager.entity.FileMetadata;
import ma.ensa.StorageManager.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StorageController.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user|test-bucket");
    }

    @Test
    void testUploadFile() throws Exception {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        when(fileService.uploadFile(any())).thenReturn(ResponseEntity.ok("File uploaded successfully"));

        mockMvc.perform(multipart("/api/files/upload")
                        .file("file", "test content".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded successfully"));
    }

    @Test
    void testListFiles() throws Exception {
        List<FileMetadata> mockFiles = Collections.singletonList(new FileMetadata(1L, "file.txt", 123L, "http://test-url"));
        when(fileService.listFiles(anyString())).thenReturn(mockFiles);

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("file.txt"))
                .andExpect(jsonPath("$[0].size").value(123));
    }

    @Test
    void testDownloadFile() throws Exception {
        byte[] mockData = "test content".getBytes();
        when(fileService.downloadFile(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(get("/api/files/download/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(content().bytes(mockData));
    }

    @Test
    void testOpenFile() throws Exception {
        byte[] mockData = "test content".getBytes();
        ByteArrayResource resource = new ByteArrayResource(mockData);
        when(fileService.downloadFile(anyString(), anyString())).thenReturn(mockData);

        mockMvc.perform(get("/api/files/open/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.txt\""))
                .andExpect(content().bytes(mockData));
    }

    @Test
    void testDeleteFile() throws Exception {
        doNothing().when(fileService).deleteFile(anyString(), anyString());

        mockMvc.perform(delete("/api/files/delete/test.txt"))
                .andExpect(status().isOk())
                .andExpect(content().string("File deleted successfully"));
    }
}
