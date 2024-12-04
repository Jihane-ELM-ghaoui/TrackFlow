package ma.ensa.StorageManager.repository;

import ma.ensa.StorageManager.entity.SharedFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedFileMetadataRepository extends JpaRepository<SharedFileMetadata, Long> {
    Optional<SharedFileMetadata> findByLinkId(String linkId);
    void deleteByLinkId(String linkId);
}

