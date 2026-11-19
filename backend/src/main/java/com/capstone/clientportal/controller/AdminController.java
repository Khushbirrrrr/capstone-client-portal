package com.capstone.clientportal.controller;

import com.capstone.clientportal.entity.Term;
import com.capstone.clientportal.service.TermGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** US08: faculty-only administration. Access is also enforced in SecurityConfig. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final TermGroupService service;
    public AdminController(TermGroupService service) { this.service = service; }

    @GetMapping("/terms")
    public List<Term> terms() { return service.terms(); }

    @PostMapping("/terms") @ResponseStatus(HttpStatus.CREATED)
    public Term createTerm(@RequestBody TermRequest body) { return service.createTerm(body.name()); }

    @PutMapping("/terms/{id}/activate")
    public Term activate(@PathVariable Long id) { return service.activate(id); }

    @GetMapping("/groups")
    public List<TermGroupService.GroupView> groups(@RequestParam Long termId) { return service.groups(termId); }

    @PostMapping("/groups") @ResponseStatus(HttpStatus.CREATED)
    public TermGroupService.GroupView createGroup(@RequestBody GroupRequest body) {
        return service.createGroup(body.name(), body.termId(), body.memberUsernames());
    }

    @DeleteMapping("/groups/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long id) { service.deleteGroup(id); }

    public record TermRequest(String name) {}
    public record GroupRequest(String name, Long termId, List<String> memberUsernames) {}
}
