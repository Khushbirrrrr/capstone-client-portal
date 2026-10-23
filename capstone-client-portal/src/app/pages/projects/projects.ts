import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './projects.html',
  styleUrl: './projects.scss'
})
export class Projects implements OnInit {
  private projectService = inject(ProjectService);

  currentTerm = 'Fall 2026';
  projects: Project[] = [];
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    this.projectService.getApprovedProjects().subscribe({
      next: (data) => {
        this.projects = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load projects. Please try again later.';
        this.loading = false;
      }
    });
  }

  get approvedProjects(): Project[] {
    return this.projects.filter(
      project =>
        project.status === 'APPROVED' &&
        project.term === this.currentTerm
    );
  }
}
