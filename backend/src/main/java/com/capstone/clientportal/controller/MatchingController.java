package com.capstone.clientportal.controller;

import com.capstone.clientportal.service.MatchingPreviewService;
import org.springframework.web.bind.annotation.*;

/** Faculty-only temporary matching preview; nothing is published or persisted. */
@RestController
@RequestMapping("/api/matching")
public class MatchingController {
    private final MatchingPreviewService service;
    public MatchingController(MatchingPreviewService service) { this.service = service; }

    @PostMapping("/generate")
    public MatchingPreviewService.MatchingPreview generate(@RequestBody MatchRequest request) {
        return service.generate(request.termId());
    }
    public record MatchRequest(Long termId) {}
}
