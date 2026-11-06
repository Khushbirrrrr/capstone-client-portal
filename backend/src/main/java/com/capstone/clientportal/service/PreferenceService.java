package com.capstone.clientportal.service;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.entity.RankedPreference;
import com.capstone.clientportal.repository.PreferenceRepository;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class PreferenceService {
    private final UserRepository users;
    private final ProjectRepository projects;
    private final PreferenceRepository preferences;
    public PreferenceService(UserRepository users, ProjectRepository projects, PreferenceRepository preferences) {
        this.users = users; this.projects = projects; this.preferences = preferences;
    }

    /** Do not accept a user ID from the client: ownership comes from the session. */
    private AppUser student(String username) {
        AppUser user = users.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session user not found"));
        if (!user.isActive() || !"STUDENT".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student account required");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<PreferenceView> list(String username, String term) {
        AppUser student = student(username);
        return preferences.findByUserIdAndTermOrderByRankOrderAsc(student.getId(), term).stream()
                .map(p -> new PreferenceView(p.getProject().getId(), p.getProject().getTitle(),
                        p.getRankOrder(), p.getSubmittedAt())).toList();
    }

    /** US11+US12: validate the entire ranking BEFORE deleting any existing preferences. */
    @Transactional
    public List<PreferenceView> submit(String username, String term, List<Long> projectIds) {
        AppUser student = student(username);
        if (projectIds == null || projectIds.isEmpty() || projectIds.size() > 5
                || projectIds.stream().anyMatch(id -> id == null) || new HashSet<>(projectIds).size() != projectIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose 1 to 5 different projects");
        }
        List<Project> approved = new ArrayList<>();
        for (Long projectId : projectIds) {
            approved.add(projects.findByIdAndStatusAndTerm(projectId, "APPROVED", term)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Only approved projects from the selected term can be ranked")));
        }
        preferences.deleteByUserIdAndTerm(student.getId(), term);
        // Ensure the old unique rank / project constraints are cleared before rewriting.
        preferences.flush();
        Instant submitted = Instant.now();
        List<RankedPreference> records = new ArrayList<>();
        for (int i = 0; i < approved.size(); i++) {
            records.add(new RankedPreference(student, approved.get(i), term, i + 1, submitted));
        }
        preferences.saveAll(records);
        return records.stream().map(p -> new PreferenceView(p.getProject().getId(),
                p.getProject().getTitle(), p.getRankOrder(), p.getSubmittedAt())).toList();
    }

    public record PreferenceView(Long projectId, String projectTitle, int rank, Instant submittedAt) {}
}
