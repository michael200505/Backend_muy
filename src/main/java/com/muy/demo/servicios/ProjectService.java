package com.muy.demo.servicios;

import com.muy.demo.modelosdto.CreateProjectRequest;
import com.muy.demo.modelosdto.UpdateProjectRequest;
import com.muy.demo.models.Project;
import com.muy.demo.models.Role;
import com.muy.demo.repositorios.ProjectRepository;
import com.muy.demo.repositorios.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projects;
    private final UserRepository users;

    public ProjectService(ProjectRepository projects, UserRepository users) {
        this.projects = projects;
        this.users = users;
    }

    @Transactional
    public Project create(CreateProjectRequest req) {
        var programmer = users.findById(req.programmerId)
                .orElseThrow(() -> new IllegalArgumentException("Programmer no existe"));
        if (programmer.getRole() != Role.PROGRAMMER) {
            throw new IllegalArgumentException("El usuario no es PROGRAMMER");
        }

        Project p = new Project();
        p.setTitle(req.title);
        p.setDescription(req.description);
        p.setRepoUrl(req.repoUrl);
        p.setDemoUrl(req.demoUrl);
        p.setProgrammer(programmer);
        p.setActive(true);
        return projects.save(p);
    }

    public List<Project> listByProgrammer(Long programmerId) {
        return projects.findByProgrammerId(programmerId);
    }

    @Transactional
    public Project update(Long id, UpdateProjectRequest req) {
        Project p = projects.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no existe"));

        if (req.title != null) p.setTitle(req.title);
        if (req.description != null) p.setDescription(req.description);
        if (req.repoUrl != null) p.setRepoUrl(req.repoUrl);
        if (req.demoUrl != null) p.setDemoUrl(req.demoUrl);
        if (req.active != null) p.setActive(req.active);

        return projects.save(p);
    }

    @Transactional
    public void delete(Long id) {
        projects.deleteById(id);
    }
}
