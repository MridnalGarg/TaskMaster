package com.mridnal.TaskMaster.tasks;

import com.mridnal.TaskMaster.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepository extends JpaRepository<Tasks, Long> {

    @Query("""
    SELECT t
    FROM Tasks t
    WHERE t.assignee.id = :userId
      AND (:status IS NULL OR t.status = :status)
      AND (
          :search IS NULL
          OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
      )
    ORDER BY t.createdAt DESC
    """)
    List<Tasks> findAssignedTasks(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("search") String search
    );

    @Query("""
    SELECT t
    FROM Tasks t
    WHERE t.project.id = :projectId
      AND (:status = '' OR t.status = :status)
      AND (
          :search = ''
          OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%'))
      )
    ORDER BY t.createdAt DESC
    """)
    List<Tasks> findAllTasksInProject(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("search") String search
    );
}
