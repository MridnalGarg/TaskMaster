package com.mridnal.TaskMaster.tasks.comments;

import com.mridnal.TaskMaster.tasks.comments.dto.CommentResponse;
import com.mridnal.TaskMaster.tasks.comments.dto.CreateCommentRequest;
import com.mridnal.TaskMaster.tasks.comments.dto.UpdateCommentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class TaskCommentsController {

    @Autowired
    private TaskCommentsService commentService;

    @PostMapping
    public CommentResponse createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        return commentService.createComment(taskId, request, authentication);
    }

    @GetMapping
    public List<CommentResponse> getComments(
            @PathVariable Long taskId,
            Authentication authentication) {
        return commentService.getComments(taskId, authentication);
    }

    @PatchMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {
        return commentService.updateComment(taskId, commentId, request, authentication);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(taskId, commentId, authentication);
    }
}
