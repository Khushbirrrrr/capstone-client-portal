package com.capstone.clientportal.entity;
import jakarta.persistence.*;
@Entity
@Table(name="final_allocations", uniqueConstraints={@UniqueConstraint(columnNames={"term_id","group_id"}),@UniqueConstraint(columnNames={"term_id","project_id"})})
public class FinalAllocation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long termId;
 @Column(nullable=false) private Long groupId;
 @Column(nullable=false) private Long projectId;
 private Long previousProjectId;
 @Column(nullable=false) private String groupName;
 @Column(nullable=false) private String projectTitle;
 @Column(length=500) private String reason;
 @Column(nullable=false) private String confirmedBy;
 @Column(nullable=false) private java.time.Instant confirmedAt;
 protected FinalAllocation() {}
 public FinalAllocation(Long termId, Long groupId, Long projectId, Long previousProjectId, String groupName, String projectTitle, String reason, String confirmedBy) {
 this.termId=termId; this.groupId=groupId; this.projectId=projectId; this.previousProjectId=previousProjectId; this.groupName=groupName; this.projectTitle=projectTitle; this.reason=reason; this.confirmedBy=confirmedBy; this.confirmedAt=java.time.Instant.now(); }
 public Long getGroupId(){return groupId;} public Long getProjectId(){return projectId;} public Long getPreviousProjectId(){return previousProjectId;}
 public String getGroupName(){return groupName;} public String getProjectTitle(){return projectTitle;} public String getReason(){return reason;} public String getConfirmedBy(){return confirmedBy;} public java.time.Instant getConfirmedAt(){return confirmedAt;}
}
