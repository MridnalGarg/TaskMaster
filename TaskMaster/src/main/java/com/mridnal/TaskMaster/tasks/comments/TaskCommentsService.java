package com.mridnal.TaskMaster.tasks.comments;

import com.mridnal.TaskMaster.tasks.Tasks;
import com.mridnal.TaskMaster.tasks.TasksRepository;
import com.mridnal.TaskMaster.tasks.comments.dto.CommentResponse;
import com.mridnal.TaskMaster.tasks.comments.dto.CreateCommentRequest;
import com.mridnal.TaskMaster.tasks.comments.dto.UpdateCommentRequest;
import com.mridnal.TaskMaster.users.UserService;
import com.mridnal.TaskMaster.users.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskCommentsService {

    @Autowired
    private TaskCommentsRepository commentsRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public CommentResponse createComment(
            Long taskId,
            CreateCommentRequest request,
            Authentication auth) {

        Users user = getAuthenticatedUser(auth);
        Tasks task = getTask(taskId);

        TaskComments comment = new TaskComments(task, user, request.comment());

        return CommentResponse.from(
                commentsRepository.save(comment)
        );
    }

    @Transactional
    public List<CommentResponse> getComments(
            Long taskId,
            Authentication auth) {

        getAuthenticatedUser(auth);
        getTask(taskId);

        return commentsRepository
                .findByTask_IdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(
            Long taskId,
            Long commentId,
            UpdateCommentRequest request,
            Authentication auth) {

        Users user = getAuthenticatedUser(auth);
        getTask(taskId);

        TaskComments comment = commentsRepository
                .findByIdAndTask_Id(commentId, taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"
                ));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only edit your own comments"
            );
        }

        comment.updateContent(request.comment());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(
            Long taskId,
            Long commentId,
            Authentication auth) {

        Users user = getAuthenticatedUser(auth);
        getTask(taskId);

        TaskComments comment = commentsRepository
                .findByIdAndTask_Id(commentId, taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Comment not found"
                ));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only delete your own comments"
            );
        }

        commentsRepository.delete(comment);
    }

    private Users getAuthenticatedUser(
            Authentication auth) {

        Users user = userService.getCurrentUser(auth);

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
                        HttpStatus.NOT_FOUND, "Task not found"
                ));
    }
}