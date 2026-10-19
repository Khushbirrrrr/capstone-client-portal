package ca.sheridan.capstone;
import jakarta.persistence.*;
@Entity
@Table(name="projects")
public class Project {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String title;
 @Column(length=2000) private String description;
 private String category;
 private boolean approved;
 private String clientName;
 protected Project(){}
 public Project(String title,String description,String category,boolean approved,String clientName){this.title=title;this.description=description;this.category=category;this.approved=approved;this.clientName=clientName;}
 public Long getId(){return id;}
 public String getTitle(){return title;}
 public String getDescription(){return description;}
 public String getCategory(){return category;}
 public boolean isApproved(){return approved;}
 public String getClientName(){return clientName;}
}
