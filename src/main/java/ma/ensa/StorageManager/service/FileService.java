package ma.ensa.StorageManager.service;

import ma.ensa.StorageManager.config.S3Config;
import ma.ensa.StorageManager.entity.FileMetaData;
import ma.ensa.StorageManager.repository.FileMetadataRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.ResponseInputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Value("${application.bucket.name}")
    private String bucketName;


    @Autowired
    private S3Config s3Config;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private FileMetadataRepository metadataRepository;

    public FileMetaData uploadFile(MultipartFile file) throws IOException {
        String key = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
            System.out.println("File uploaded successfully to S3 with key: " + key);
        } catch (S3Exception e) {
            System.err.println("S3 upload failed: " + e.awsErrorDetails().errorMessage());
            throw e;
        }

        FileMetaData metadata = new FileMetaData();
        metadata.setFilename(file.getOriginalFilename());
        metadata.setS3Key(key);
        metadata.setContentType(file.getContentType());
        metadata.setSize(file.getSize());
        metadata.setUploadTimestamp(LocalDateTime.now());

        return metadataRepository.save(metadata);
    }

    public List<FileMetaData> listFiles() {
        return metadataRepository.findAll();
    }

    public byte[] downloadFile(Long id) throws IOException {
        FileMetaData metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        // Get the object as a ResponseInputStream from S3
        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket("trackflowstoragemanager")
                        .key(metadata.getS3Key())
                        .build()
        );

        // Convert the ResponseInputStream into a byte array
        try (InputStream inputStream = s3Object) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }

    public void deleteFile(Long id) {
        FileMetaData metadata = metadataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found"));

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket("trackflowstoragemanager")
                        .key(metadata.getS3Key())
                        .build()
        );

        metadataRepository.delete(metadata);
    }
}

