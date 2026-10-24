package com.mridnal.TaskMaster.projects.memberships;

import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMemberships, Long> {
    List<ProjectMemberships> findByUser_Id(Long id);

    boolean existsByProjectAndUser(Projects project, Users user);
}
