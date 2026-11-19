package com.capstone.clientportal.service;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.Project;
import com.capstone.clientportal.entity.StudentGroup;
import com.capstone.clientportal.repository.GroupRepository;
import com.capstone.clientportal.repository.PreferenceRepository;
import com.capstone.clientportal.repository.ProjectRepository;
import com.capstone.clientportal.repository.TermRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sprint 3 prototype: faculty previews suggestions, not final assignments.
 * A ranked project earns 5 points for first place, 4 for second, down to 1 for fifth.
 * Scores are added across group members. Groups are considered by ID, and a project
 * may only be suggested once. This greedy approach is deterministic but NOT globally
 * optimal or a substitute for faculty approval. Clarify matching policy with the client.
 */
@Service
public class MatchingPreviewService {
    private final TermRepository terms;
    private final GroupRepository groups;
    private final ProjectRepository projects;
    private final PreferenceRepository preferences;

    public MatchingPreviewService(TermRepository terms, GroupRepository groups,
            ProjectRepository projects, PreferenceRepository preferences) {
        this.terms = terms; this.groups = groups;
        this.projects = projects; this.preferences = preferences;
    }

    @Transactional(readOnly = true)
    public MatchingPreview generate(Long termId) {
        if (termId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Select a term");
        var term = terms.findById(termId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Term not found"));
        var approved = projects.findByStatusAndTerm("APPROVED", term.getName());
        Map<Long, Project> byId = new HashMap<>();
        for (Project project : approved) byId.put(project.getId(), project);
        List<StudentGroup> orderedGroups = new ArrayList<>(groups.findByTermId(termId));
        orderedGroups.sort(Comparator.comparing(StudentGroup::getId));
        Set<Long> assignedProjectIds = new HashSet<>();
        List<Recommendation> suggestions = new ArrayList<>();

        for (StudentGroup group : orderedGroups) {
            Map<Long, Integer> scores = new HashMap<>();
            for (AppUser member : group.getMembers()) {
                if (!member.isActive() || !"STUDENT".equals(member.getRole())) continue;
                for (var preference : preferences.findByUserIdAndTermOrderByRankOrderAsc(
                        member.getId(), term.getName())) {
                    Long projectId = preference.getProject().getId();
                    int rank = preference.getRankOrder();
                    if (byId.containsKey(projectId) && rank >= 1 && rank <= 5) {
                        scores.merge(projectId, 6 - rank, Integer::sum);
                    }
                }
            }
            Long chosenId = scores.entrySet().stream()
                    .filter(entry -> !assignedProjectIds.contains(entry.getKey()) && entry.getValue() > 0)
                    .sorted(Comparator.<Map.Entry<Long, Integer>>comparingInt(Map.Entry::getValue)
                            .reversed().thenComparingLong(Map.Entry::getKey))
                    .map(Map.Entry::getKey).findFirst().orElse(null);
            if (chosenId == null) {
                String reason = scores.isEmpty() ? "No group member has submitted eligible preferences"
                        : "No ranked project remains available";
                suggestions.add(new Recommendation(group.getId(), group.getName(), null, null,
                        0, "UNRESOLVED", reason));
            } else {
                assignedProjectIds.add(chosenId);
                suggestions.add(new Recommendation(group.getId(), group.getName(), chosenId,
                        byId.get(chosenId).getTitle(), scores.get(chosenId), "RECOMMENDED",
                        "Preview only — requires faculty review"));
            }
        }
        return new MatchingPreview(termId, term.getName(), suggestions);
    }

    public record MatchingPreview(Long termId, String term, List<Recommendation> recommendations) {}
    public record Recommendation(Long groupId, String groupName, Long projectId,
            String projectTitle, int score, String status, String note) {}
}
