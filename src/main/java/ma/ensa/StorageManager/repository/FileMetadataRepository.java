package ma.ensa.StorageManager.repository;

import ma.ensa.StorageManager.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
