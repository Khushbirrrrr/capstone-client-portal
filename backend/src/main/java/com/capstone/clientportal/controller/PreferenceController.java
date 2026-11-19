package com.capstone.clientportal.controller;

import com.capstone.clientportal.service.PreferenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/preferences")
public class PreferenceController {
    private final PreferenceService preferences;
    @Value("${portal.default-term:Fall 2026}") private String defaultTerm;
    public PreferenceController(PreferenceService preferences) { this.preferences = preferences; }

    @GetMapping
    public List<PreferenceService.PreferenceView> list(Principal principal,
            @RequestParam(required = false) String term) {
        return preferences.list(principal.getName(), chooseTerm(term));
    }

    /** Re-submitting replaces this user's earlier preferences for this term, not other users' rankings. */
    @PutMapping
    public List<PreferenceService.PreferenceView> submit(Principal principal, @RequestBody RankingRequest body) {
        return preferences.submit(principal.getName(), chooseTerm(body.term()), body.projectIds());
    }

    private String chooseTerm(String term) {
        return term == null || term.isBlank() ? defaultTerm : term;
    }
    public record RankingRequest(String term, List<Long> projectIds) {}
}
