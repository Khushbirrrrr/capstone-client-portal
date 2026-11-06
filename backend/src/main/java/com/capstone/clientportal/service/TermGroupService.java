package com.capstone.clientportal.service;

import com.capstone.clientportal.entity.AppUser;
import com.capstone.clientportal.entity.StudentGroup;
import com.capstone.clientportal.entity.Term;
import com.capstone.clientportal.repository.GroupRepository;
import com.capstone.clientportal.repository.TermRepository;
import com.capstone.clientportal.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TermGroupService {
    private final TermRepository terms;
    private final GroupRepository groups;
    private final UserRepository users;
    public TermGroupService(TermRepository terms, GroupRepository groups, UserRepository users) {
        this.terms = terms; this.groups = groups; this.users = users;
    }
    public List<Term> terms() { return terms.findAll(); }

    @Transactional
    public Term createTerm(String name) {
        String cleaned = name == null ? "" : name.trim();
        if (cleaned.length() < 4 || cleaned.length() > 60) throw bad("Term name must have 4 to 60 characters");
        if (terms.findByName(cleaned).isPresent()) throw bad("Term already exists");
        return terms.save(new Term(cleaned, terms.count() == 0));
    }

    @Transactional
    public Term activate(Long id) {
        Term chosen = terms.findById(id).orElseThrow(() -> notFound("Term not found"));
        for (Term t : terms.findAll()) t.setActive(t.getId().equals(chosen.getId()));
        terms.flush();
        return chosen;
    }

    @Transactional(readOnly = true)
    public List<GroupView> groups(Long termId) {
        return groups.findByTermId(termId).stream().map(this::view).toList();
    }

    @Transactional
    public GroupView createGroup(String name, Long termId, List<String> usernames) {
        String cleaned = name == null ? "" : name.trim();
        if (cleaned.length() < 3 || cleaned.length() > 80) throw bad("Group name must have 3 to 80 characters");
        if (termId == null) throw bad("Choose a term");
        Term term = terms.findById(termId).orElseThrow(() -> notFound("Term not found"));
        if (groups.existsByNameAndTermId(cleaned, termId)) throw bad("Group already exists in this term");
        if (usernames == null || usernames.isEmpty()) throw bad("Provide at least one student username");
        List<AppUser> members = new ArrayList<>();
        for (String username : usernames) {
            String key = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
            AppUser member = users.findByUsername(key).orElseThrow(() -> bad("Unknown student username: " + key));
            if (!"STUDENT".equals(member.getRole()) || !member.isActive() || members.contains(member)) {
                throw bad("Group members must be distinct active students");
            }
            members.add(member);
        }
        StudentGroup group = new StudentGroup();
        group.setName(cleaned); group.setTerm(term); group.setMembers(members);
        return view(groups.save(group));
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groups.existsById(id)) throw notFound("Group not found");
        groups.deleteById(id);
    }

    private GroupView view(StudentGroup group) {
        return new GroupView(group.getId(), group.getName(), group.getTerm().getId(),
                group.getMembers().stream().map(AppUser::getUsername).toList());
    }
    private ResponseStatusException bad(String msg) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg); }
    private ResponseStatusException notFound(String msg) { return new ResponseStatusException(HttpStatus.NOT_FOUND, msg); }
    public record GroupView(Long id, String name, Long termId, List<String> members) {}
}
