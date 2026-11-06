import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project';
import { CurrentTermService } from '../../services/current-term';

@Component({ selector: 'app-project-detail', standalone: true, imports: [RouterLink],
  templateUrl: './project-detail.html', styleUrl: './project-detail.scss' })
export class ProjectDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private api = inject(ProjectService);
  private termApi = inject(CurrentTermService);
  project?: Project;
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!Number.isSafeInteger(id) || id <= 0) {
      this.errorMessage = 'Invalid project ID.';
      this.loading = false;
      return;
    }
    this.termApi.get().subscribe({
      next: term => this.api.getApprovedProject(id, term.name).subscribe({
        next: project => { this.project = project; this.loading = false; },
        error: error => {
          this.loading = false;
          this.errorMessage = error.status === 404 ? 'This project is unavailable or not approved.' :
            'Unable to load project details.';
        }
      }),
      error: () => { this.errorMessage = 'Unable to load the current term.'; this.loading = false; }
    });
  }
}
