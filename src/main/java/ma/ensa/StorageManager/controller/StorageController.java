package ma.ensa.StorageManager.controller;


import ma.ensa.StorageManager.entity.FileMetadata;
import ma.ensa.StorageManager.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("http://localhost:3000")
public class StorageController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> listFiles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        List<FileMetadata> files = fileService.listFiles(userId);
        return ResponseEntity.ok(files);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("fileName") String fileName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        byte[] fileData = fileService.downloadFile(userId, fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData);
    }

    @GetMapping("/open/{fileName}")
    public ResponseEntity<Resource> openFile(@PathVariable("fileName") String fileName) {
        // Get the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        // Fetch the file data (you need to implement this in your service)
        byte[] fileData = fileService.downloadFile(userId, fileName);

        // Determine the file's MIME type based on the file name
        String contentType;
        if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/png"; // Adjust for your image type
        } else {
            contentType = "application/octet-stream"; // Fallback for unknown types
        }

        // Convert the byte array into a resource
        ByteArrayResource resource = new ByteArrayResource(fileData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .contentLength(fileData.length)
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable("fileName") String fileName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        try {
            fileService.deleteFile(userId, fileName); // Ensure this service method is implemented
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete file");
        }
    }


}
