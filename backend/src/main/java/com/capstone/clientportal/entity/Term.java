package com.capstone.clientportal.entity;

import jakarta.persistence.*;

/** Faculty-managed teaching term for project browsing and preferences (US08). */
@Entity
@Table(name = "capstone_terms")
public class Term {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 60)
    private String name;
    @Column(nullable = false)
    private boolean active;

    public Term() {}
    public Term(String name, boolean active) { this.name = name; this.active = active; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
