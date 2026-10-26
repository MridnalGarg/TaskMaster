package com.mridnal.TaskMaster.projects.memberships;

import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectMembershipsService {

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    public void createProjectMembership(Projects project, Users user){
        if(!projectMembershipRepository
                .existsByProjectAndUser(project, user)){
            ProjectMemberships projectMemberships = new ProjectMemberships(project, user);
            projectMembershipRepository.save(projectMemberships);
        }
    }
}
