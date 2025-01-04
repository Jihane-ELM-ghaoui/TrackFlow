package ma.ensa.StorageManager.service;

import ma.ensa.StorageManager.entity.FileMetadata;
import ma.ensa.StorageManager.entity.SharedFileMetadata;
import ma.ensa.StorageManager.kafkaConfig.KafkaProducer;
import ma.ensa.StorageManager.repository.SharedFileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private FileService fileService;

    @Mock
    private MultipartFile mockFile;

    @Mock
    private Authentication authentication;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private SharedFileMetadataRepository sharedFileMetadataRepository;

    @Captor
    private ArgumentCaptor<SharedFileMetadata> metadataCaptor;

    @Captor
    private ArgumentCaptor<PutObjectRequest> putObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<ListObjectsV2Request> listObjectsV2RequestCaptor;

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequestCaptor;

    @Captor
    private ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;

    private final String userId = "auth0|670a49f45fb7f3ba271f916a";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn(userId);
        fileService = spy(fileService);
    }

    @Test
    void testUploadFile() throws Exception {
        // Arrange
        String bucketName = "670a49f45fb7f3ba271f916a";
        String fileName = "test.txt";

        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        // Act
        ResponseEntity<String> response = fileService.uploadFile(mockFile);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(bucketName));

        verify(s3Client).putObject(putObjectRequestCaptor.capture(), (RequestBody) any());
        PutObjectRequest capturedRequest = putObjectRequestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(fileName, capturedRequest.key());

        // Verify Kafka message
        verify(kafkaProducer).sendFileUploadMessage(userId);
    }

    @Test
    void testListFiles() {
        // Arrange
        String bucketName = "670a49f45fb7f3ba271f916a";

        ListObjectsV2Response mockResponse = ListObjectsV2Response.builder()
                .contents(Collections.singletonList(S3Object.builder().key("file.txt").size(123L).build()))
                .build();
        when(s3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResponse);

        // Act
        List<FileMetadata> files = fileService.listFiles(userId);

        // Assert
        assertNotNull(files);
        assertEquals(1, files.size());
        assertEquals("file.txt", files.get(0).getName());
        assertEquals(123L, files.get(0).getSize());

        verify(s3Client).listObjectsV2(listObjectsV2RequestCaptor.capture());
        ListObjectsV2Request capturedRequest = listObjectsV2RequestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
    }

    @Test
    void testDownloadFile() {
        // Arrange
        String bucketName = "670a49f45fb7f3ba271f916a";
        String fileName = "file.txt";
        byte[] fileContent = "test content".getBytes();

        ResponseBytes<GetObjectResponse> mockResponseBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), fileContent);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockResponseBytes);

        // Act
        byte[] downloadedFile = fileService.downloadFile(userId, fileName);

        // Assert
        assertNotNull(downloadedFile);
        assertArrayEquals(fileContent, downloadedFile);

        verify(s3Client).getObjectAsBytes(getObjectRequestCaptor.capture());
        GetObjectRequest capturedRequest = getObjectRequestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(fileName, capturedRequest.key());

        // Verify Kafka message
        verify(kafkaProducer).sendFileDownloadMessage(userId);
    }

    @Test
    void testDeleteFile() {
        // Arrange
        String bucketName = "670a49f45fb7f3ba271f916a";
        String fileName = "file.txt";

        // Act
        fileService.deleteFile(userId, fileName);

        // Assert
        verify(s3Client).deleteObject(deleteObjectRequestCaptor.capture());
        DeleteObjectRequest capturedRequest = deleteObjectRequestCaptor.getValue();
        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(fileName, capturedRequest.key());

        // Verify Kafka message
        verify(kafkaProducer).sendFileDeleteMessage(userId);
    }

    /////////////////////////////////////////////////


    @Test
    void testShareFile() {
        // Arrange
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String fileName = "test.txt";
        String expectedBaseUrl = "http://localhost:8888/storage-service/api/files/shared/";

        // Act
        String shareLink = fileService.shareFile(userId, fileName);

        // Assert
        assertNotNull(shareLink);
        assertTrue(shareLink.startsWith(expectedBaseUrl));

        verify(sharedFileMetadataRepository).save(metadataCaptor.capture());
        SharedFileMetadata capturedMetadata = metadataCaptor.getValue();

        assertEquals(userId, capturedMetadata.getUserId());
        assertEquals(fileName, capturedMetadata.getFileName());
        assertNotNull(capturedMetadata.getLinkId());
        assertTrue(capturedMetadata.getExpiryTime().isAfter(Instant.now()));
    }

    @Test
    void testGetSharedFile_ValidLink() {
        // Arrange
        String linkId = UUID.randomUUID().toString();
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String fileName = "test.txt";

        SharedFileMetadata mockMetadata = new SharedFileMetadata(
                userId,
                fileName,
                Instant.now().plusSeconds(24 * 60 * 60), // Valid expiry
                linkId
        );
        when(sharedFileMetadataRepository.findByLinkId(linkId)).thenReturn(Optional.of(mockMetadata));

        byte[] mockFileContent = "file content".getBytes();
        doReturn(mockFileContent).when(fileService).downloadFile(userId, fileName); // Spy allows mocking this call

        // Act
        byte[] result = fileService.getSharedFile(linkId);

        // Assert
        assertNotNull(result);
        assertArrayEquals(mockFileContent, result);

        verify(sharedFileMetadataRepository, never()).deleteByLinkId(any());
    }

    @Test
    void testGetSharedFile_ExpiredLink() {
        // Arrange
        String linkId = UUID.randomUUID().toString();
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String fileName = "test.txt";

        SharedFileMetadata expiredMetadata = new SharedFileMetadata(
                userId,
                fileName,
                Instant.now().minusSeconds(60), // Already expired
                linkId
        );
        when(sharedFileMetadataRepository.findByLinkId(linkId)).thenReturn(Optional.of(expiredMetadata));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileService.getSharedFile(linkId));
        assertEquals("Link has expired", exception.getMessage());

        verify(sharedFileMetadataRepository).deleteByLinkId(linkId);
    }

    @Test
    void testGetSharedFile_InvalidLink() {
        // Arrange
        String linkId = UUID.randomUUID().toString();
        when(sharedFileMetadataRepository.findByLinkId(linkId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileService.getSharedFile(linkId));
        assertEquals("Invalid or expired link", exception.getMessage());

        verify(sharedFileMetadataRepository, never()).deleteByLinkId(any());
    }

    @Test
    void testGetFileNameFromLink_ValidLink() {
        // Arrange
        String linkId = UUID.randomUUID().toString();
        String fileName = "test.txt";

        SharedFileMetadata mockMetadata = new SharedFileMetadata(
                "auth0|670a49f45fb7f3ba271f916a",
                fileName,
                Instant.now().plusSeconds(24 * 60 * 60),
                linkId
        );
        when(sharedFileMetadataRepository.findByLinkId(linkId)).thenReturn(Optional.of(mockMetadata));

        // Act
        String result = fileService.getFileNameFromLink(linkId);

        // Assert
        assertNotNull(result);
        assertEquals(fileName, result);
    }

    @Test
    void testGetFileNameFromLink_InvalidLink() {
        // Arrange
        String linkId = UUID.randomUUID().toString();
        when(sharedFileMetadataRepository.findByLinkId(linkId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileService.getFileNameFromLink(linkId));
        assertEquals("Invalid link ID", exception.getMessage());
    }

}
