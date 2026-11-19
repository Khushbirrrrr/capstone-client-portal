package com.capstone.clientportal.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Faculty-managed group and enrolled portal usernames (US08). */
@Entity
@Table(name = "student_groups", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "term_id"}))
public class StudentGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String name;
    @ManyToOne(optional = false)
    private Term term;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_group_members", joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<AppUser> members = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Term getTerm() { return term; }
    public void setTerm(Term term) { this.term = term; }
    public List<AppUser> getMembers() { return members; }
    public void setMembers(List<AppUser> members) { this.members = members; }
}
