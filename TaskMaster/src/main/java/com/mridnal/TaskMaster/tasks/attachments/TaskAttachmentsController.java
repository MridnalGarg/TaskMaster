package com.mridnal.TaskMaster.tasks.attachments;

import com.mridnal.TaskMaster.tasks.attachments.dto.AttachmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/attachments")
@Tag(name = "Task Attachments", description = "Upload and manage task files")
public class TaskAttachmentsController {

    @Autowired
    private TaskAttachmentsService attachmentService;

    @Autowired
    private AttachmentStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an attachment to a task")
    public AttachmentResponse upload(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        return attachmentService.upload(
                taskId, file, authentication
        );
    }

    @GetMapping
    @Operation(summary = "List attachments for a task")
    public List<AttachmentResponse> list(
            @PathVariable Long taskId,
            Authentication authentication) {

        return attachmentService.list(taskId, authentication);
    }

    @GetMapping("/{attachmentId}")
    @Operation(summary = "Get attachment metadata")
    public AttachmentResponse getMetadata(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId,
            Authentication authentication) {

        return attachmentService.getMetadata(
                taskId, attachmentId, authentication
        );
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download an attachment")
    public ResponseEntity<Resource> download(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId,
            Authentication authentication) {

        TaskAttachments attachment = attachmentService.getForDownload(
                taskId, attachmentId, authentication
        );

        Resource resource = storageService.loadAsResource(
                attachment.getStorageKey()
        );

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(
                        attachment.getOriginalFilename(),
                        StandardCharsets.UTF_8
                )
                .build();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .header("X-Content-Type-Options", "nosniff")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(attachment.getFileSize())
                .body(resource);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete an uploaded attachment")
    public ResponseEntity<Void> delete(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId,
            Authentication authentication) {

        attachmentService.delete(
                taskId, attachmentId, authentication
        );

        return ResponseEntity.noContent().build();
    }
}

