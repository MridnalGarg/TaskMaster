package com.mridnal.TaskMaster.tasks.attachments;

import com.mridnal.TaskMaster.projects.memberships.ProjectMembershipRepository;
import com.mridnal.TaskMaster.tasks.Tasks;
import com.mridnal.TaskMaster.tasks.TasksRepository;
import com.mridnal.TaskMaster.tasks.attachments.dto.AttachmentResponse;
import com.mridnal.TaskMaster.users.UserService;
import com.mridnal.TaskMaster.users.Users;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
public class TaskAttachmentsService {

    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;

    @Autowired
    private TaskAttachmentsRepository attachmentRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private ProjectMembershipRepository membershipRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AttachmentStorageService storageService;

    @Transactional
    public AttachmentResponse upload(
            Long taskId,
            MultipartFile file,
            Authentication authentication) {

        Users user = getAuthenticatedUser(authentication);
        Tasks task = getTask(taskId);
        verifyTaskAccess(task, user);

        validateFile(file);

        String originalFilename = sanitizeFilename(
                file.getOriginalFilename()
        );

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String storageKey = null;

        try {
            storageKey = storageService.store(file);

            TaskAttachments attachment = new TaskAttachments(
                    task,
                    user,
                    originalFilename,
                    storageKey,
                    contentType,
                    file.getSize()
            );

            return AttachmentResponse.from(
                    attachmentRepository.saveAndFlush(attachment)
            );

        } catch (IOException e) {
            deleteStoredFileQuietly(storageKey);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not store attachment",
                    e
            );
        } catch (RuntimeException e) {
            deleteStoredFileQuietly(storageKey);
            throw e;
        }
    }

    @Transactional
    public List<AttachmentResponse> list(
            Long taskId,
            Authentication authentication) {

        Users user = getAuthenticatedUser(authentication);
        Tasks task = getTask(taskId);
        verifyTaskAccess(task, user);

        return attachmentRepository
                .findByTask_IdOrderByUploadedAtDesc(taskId)
                .stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    @Transactional
    public AttachmentResponse getMetadata(
            Long taskId,
            Long attachmentId,
            Authentication authentication) {

        Users user = getAuthenticatedUser(authentication);
        Tasks task = getTask(taskId);
        verifyTaskAccess(task, user);

        return AttachmentResponse.from(
                getAttachment(taskId, attachmentId)
        );
    }

    @Transactional
    public TaskAttachments getForDownload(
            Long taskId,
            Long attachmentId,
            Authentication authentication) {

        Users user = getAuthenticatedUser(authentication);
        Tasks task = getTask(taskId);
        verifyTaskAccess(task, user);

        return getAttachment(taskId, attachmentId);
    }

    @Transactional
    public void delete(
            Long taskId,
            Long attachmentId,
            Authentication authentication) {

        Users user = getAuthenticatedUser(authentication);
        Tasks task = getTask(taskId);
        verifyTaskAccess(task, user);

        TaskAttachments attachment = getAttachment(
                taskId, attachmentId
        );

        // Only the uploader can delete their attachment.
        if (!attachment.getUploader().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete attachments you uploaded"
            );
        }

        try {
            storageService.delete(attachment.getStorageKey());
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not delete attachment file",
                    e
            );
        }

        attachmentRepository.delete(attachment);
    }

    private Users getAuthenticatedUser(Authentication authentication) {
        Users user = userService.getCurrentUser(authentication);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"
            );
        }

        return user;
    }

    private Tasks getTask(Long taskId) {
        return tasksRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found"
                ));
    }

    private TaskAttachments getAttachment(
            Long taskId, Long attachmentId) {

        return attachmentRepository
                .findByIdAndTask_Id(attachmentId, taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Attachment not found"
                ));
    }

    private void verifyTaskAccess(Tasks task, Users user) {
        boolean isMember = membershipRepository.existsByProjectAndUser(
                task.getProject(), user
        );

        if (!isMember) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this task"
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A non-empty file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum attachment size is 10 MB"
            );
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Filename is required"
            );
        }

        // Handle both Unix and Windows path separators.
        String sanitized = filename.replace('\\', '/');
        sanitized = sanitized.substring(sanitized.lastIndexOf('/') + 1);

        if (sanitized.isBlank() || sanitized.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid filename"
            );
        }

        return sanitized;
    }

    private void deleteStoredFileQuietly(String storageKey) {
        if (storageKey == null) {
            return;
        }

        try {
            storageService.delete(storageKey);
        } catch (IOException ignored) {
            // Production implementation should log this and retry cleanup.
        }
    }
}
