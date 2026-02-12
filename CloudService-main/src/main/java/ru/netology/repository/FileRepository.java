package ru.netology.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.netology.entity.FileEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByUserUsername(String username);

    Optional<FileEntity> findByFilenameAndUserUsername(String filename, String username);

    @Modifying
    @Transactional
    @Query("UPDATE FileEntity f SET f.filename = :newFilename WHERE f.filename = :filename AND f.user.username = :username")
    int editFileName(@Param("filename") String fileName,
                     @Param("newFilename") String newFilename,
                     @Param("username") String username);

    // ИСПРАВЛЕНО: Используем Pageable для limit
    List<FileEntity> findByUserUsernameOrderById(String username, Pageable pageable);

    void deleteByFilenameAndUserUsername(String filename, String username);

    boolean existsByFilenameAndUserUsername(String filename, String username);
}