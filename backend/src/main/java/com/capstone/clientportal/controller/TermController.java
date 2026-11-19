package com.capstone.clientportal.controller;

import com.capstone.clientportal.repository.TermRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/** Exposes only the current teaching term, not faculty administration privileges. */
@RestController
@RequestMapping("/api/terms")
public class TermController {
    private final TermRepository terms;
    @Value("${portal.default-term:Fall 2026}") private String defaultTerm;
    public TermController(TermRepository terms) { this.terms = terms; }
    @GetMapping("/current")
    public Map<String, String> current() {
        return Map.of("name", terms.findByActiveTrue().map(t -> t.getName()).orElse(defaultTerm));
    }
}
