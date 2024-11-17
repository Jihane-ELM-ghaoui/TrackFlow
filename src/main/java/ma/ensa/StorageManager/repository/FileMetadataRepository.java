package ma.ensa.StorageManager.repository;

import ma.ensa.StorageManager.entity.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetaData, Long> {
}
