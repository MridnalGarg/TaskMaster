package com.mridnal.TaskMaster.tasks.attachments;

import com.mridnal.TaskMaster.exception.AttachmentStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AttachmentStorageService {

    private final Path storageDirectory;

    public AttachmentStorageService(
            @Value("${app.attachments.storage-dir:../uploads}")
            String storageDirectory) throws IOException {

        this.storageDirectory = Path.of(storageDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.storageDirectory);
    }

    public String store(MultipartFile file) throws IOException {
        String storageKey = UUID.randomUUID().toString();
        Path destination = resolvePath(storageKey);

        try (var inputStream = file.getInputStream()) {
            Files.copy(
                    inputStream,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return storageKey;
    }

    public Resource loadAsResource(String storageKey) {
        Path path = resolvePath(storageKey);
        Resource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new AttachmentStorageException(
                    "Attachment file is unavailable"
            );
        }

        return resource;
    }

    public void delete(String storageKey) throws IOException {
        Files.deleteIfExists(resolvePath(storageKey));
    }

    private Path resolvePath(String storageKey) {
        // Storage keys are generated UUIDs, never user-provided filenames.
        UUID.fromString(storageKey);

        Path resolved = storageDirectory.resolve(storageKey).normalize();

        if (!resolved.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid storage key");
        }

        return resolved;
    }
}
