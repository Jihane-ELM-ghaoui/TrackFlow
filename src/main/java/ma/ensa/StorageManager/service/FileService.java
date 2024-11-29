package ma.ensa.StorageManager.service;

import ma.ensa.StorageManager.entity.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private S3Client s3Client;

    public ResponseEntity<String> uploadFile(MultipartFile file) {
        try {
            // Retrieve the current authentication object
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // Extract the userId from the authentication object
            String userId = auth.getName();
            System.out.println("Authenticated User: " + userId);

            String bucketName = userId.split("\\|")[1];
            String fileName = file.getOriginalFilename();

            // Upload file to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes())
            );

            return ResponseEntity.ok("File uploaded successfully to bucket: " + bucketName);

        } catch (JwtValidationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
        } catch (S3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S3 Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error: " + e.getMessage());
        }
    }

    public List<FileMetadata> listFiles(String userId) {
        String bucketName = userId.split("\\|")[1];

        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        List<FileMetadata> files = new ArrayList<>();
        try {
            ListObjectsV2Response result = s3Client.listObjectsV2(listObjectsRequest);
            for (S3Object object : result.contents()) {
                FileMetadata fileMetadata = new FileMetadata();
                fileMetadata.setName(object.key());
                fileMetadata.setSize(object.size());
                files.add(fileMetadata);
            }
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list files from S3 bucket", e);
        }

        return files;
    }

    public byte[] downloadFile(String userId, String fileName) {
        String bucketName = userId.split("\\|")[1];

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();

        } catch (S3Exception e) {
            throw new RuntimeException("Failed to download file from S3 bucket", e);
        }
    }

    public void deleteFile(String userId, String fileName) {
        String bucketName = userId.split("\\|")[1];

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete file from S3 bucket", e);
        }
    }
}