package com.mridnal.TaskMaster.tasks.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskCommentsRepository extends JpaRepository<TaskComments, Long> {

    List<TaskComments> findByTask_IdOrderByCreatedAtAsc(Long taskId);

    Optional<TaskComments> findByIdAndTask_Id(
            Long commentId,
            Long taskId
    );

}
