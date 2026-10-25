package com.mridnal.TaskMaster.tasks.comments;

import com.mridnal.TaskMaster.tasks.comments.dto.CommentResponse;
import com.mridnal.TaskMaster.tasks.comments.dto.CreateCommentRequest;
import com.mridnal.TaskMaster.tasks.comments.dto.UpdateCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@Tag(name = "Comment Controller", description = "Create and Read comments on tasks")
public class TaskCommentsController {

    @Autowired
    private TaskCommentsService commentService;

    @PostMapping
    @Operation(summary = "Create a comment")
    public CommentResponse createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        return commentService.createComment(taskId, request, authentication);
    }

    @GetMapping
    @Operation(summary = "Get all Comments in task")
    public List<CommentResponse> getComments(
            @PathVariable Long taskId,
            Authentication authentication) {
        return commentService.getComments(taskId, authentication);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update a comment")
    public CommentResponse updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {
        return commentService.updateComment(taskId, commentId, request, authentication);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment")
    public void deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(taskId, commentId, authentication);
    }
}
