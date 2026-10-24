package com.mridnal.TaskMaster.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, Long> {

    @Query("""
        SELECT pm.project
        FROM ProjectMemberships pm
        JOIN pm.project p
        WHERE pm.user.id = :userId
        """)
    List<Projects> findProjectsByUserId(
            @Param("userId") Long userId
    );
}
