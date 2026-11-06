package com.capstone.clientportal.entity;

import jakarta.persistence.*;
import java.time.Instant;

/** One ranked approved project for one student and term (US11/US12). */
@Entity
@Table(name = "ranked_preferences", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "term", "rank_order"}),
    @UniqueConstraint(columnNames = {"user_id", "term", "project_id"})
})
public class RankedPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(optional = false) @JoinColumn(name = "project_id")
    private Project project;
    @Column(nullable = false)
    private String term;
    @Column(name = "rank_order", nullable = false)
    private int rankOrder;
    @Column(nullable = false)
    private Instant submittedAt;

    public RankedPreference() {}
    public RankedPreference(AppUser user, Project project, String term, int rankOrder, Instant submittedAt) {
        this.user = user; this.project = project; this.term = term;
        this.rankOrder = rankOrder; this.submittedAt = submittedAt;
    }
    public Project getProject() { return project; }
    public String getTerm() { return term; }
    public int getRankOrder() { return rankOrder; }
    public Instant getSubmittedAt() { return submittedAt; }
}
