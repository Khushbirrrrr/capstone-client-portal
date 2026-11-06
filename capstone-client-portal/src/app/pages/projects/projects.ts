import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project';
import { CurrentTermService } from '../../services/current-term';

@Component({ selector: 'app-projects', standalone: true, imports: [RouterLink],
  templateUrl: './projects.html', styleUrl: './projects.scss' })
export class Projects implements OnInit {
  private projectsApi = inject(ProjectService);
  private currentTermApi = inject(CurrentTermService);
  currentTerm = 'Fall 2026';
  projects: Project[] = [];
  loading = true;
  errorMessage = '';
  ngOnInit(): void {
    this.currentTermApi.get().subscribe({
      next: ({ name }) => { this.currentTerm = name; this.loadProjects(); },
      error: () => { this.errorMessage = 'Unable to load the current term.'; this.loading = false; }
    });
  }
  private loadProjects(): void {
    this.projectsApi.getApprovedProjects(this.currentTerm).subscribe({
      next: data => { this.projects = data; this.loading = false; },
      error: () => { this.errorMessage = 'Unable to load the project catalogue.'; this.loading = false; }
    });
  }
  get approvedProjects(): Project[] {
    return this.projects.filter(p => p.status === 'APPROVED');
  }
}
