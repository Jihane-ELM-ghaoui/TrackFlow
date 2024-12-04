package ma.ensa.StorageManager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SharedFileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String fileName;
    private Instant expiryTime;
    private String linkId;


    // Constructor without ID
    public SharedFileMetadata(String userId, String fileName, Instant expiryTime, String linkId) {
        this.userId = userId;
        this.fileName = fileName;
        this.expiryTime = expiryTime;
        this.linkId = linkId;
    }

}
