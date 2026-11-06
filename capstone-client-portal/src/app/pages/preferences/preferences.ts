import { Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project';
import { PreferenceService } from '../../services/preference';
import { CurrentTermService } from '../../services/current-term';
import { RankedPreference } from '../../models/preference.model';

@Component({ selector: 'app-preferences', standalone: true, imports: [RouterLink, DatePipe],
  templateUrl: './preferences.html', styleUrl: './preferences.scss' })
export class Preferences implements OnInit {
  private projectsApi = inject(ProjectService);
  private api = inject(PreferenceService);
  private route = inject(ActivatedRoute);
  private termApi = inject(CurrentTermService);
  term = 'Fall 2026';
  projects: Project[] = [];
  selected: number[] = [];
  submitted: RankedPreference[] = [];
  loading = true;
  saving = false;
  message = '';
  errorMessage = '';

  ngOnInit(): void {
    this.termApi.get().subscribe({
      next: ({ name }) => { this.term = name; this.loadPreferences(); },
      error: () => { this.errorMessage = 'Unable to load the current term.'; this.loading = false; }
    });
  }
  private loadPreferences(): void {
    this.projectsApi.getApprovedProjects(this.term).subscribe({
      next: projects => {
        this.projects = projects;
        this.api.list(this.term).subscribe({
          next: preferences => {
            this.submitted = preferences;
            this.selected = preferences.map(p => p.projectId).filter(id => projects.some(p => p.id === id));
            const toAdd = Number(this.route.snapshot.queryParamMap.get('add'));
            if (projects.some(p => p.id === toAdd) && !this.selected.includes(toAdd) && this.selected.length < 5) {
              this.selected.push(toAdd);
              this.message = 'Project added to your draft ranking. Submit to save it.';
            }
            this.loading = false;
          },
          error: () => { this.loading = false; this.errorMessage = 'Unable to load your saved preferences.'; }
        });
      },
      error: () => { this.loading = false; this.errorMessage = 'Unable to load projects.'; }
    });
  }

  project(id: number): Project | undefined { return this.projects.find(p => p.id === id); }
  get available(): Project[] { return this.projects.filter(p => !this.selected.includes(p.id)); }
  add(id: number): void {
    if (this.selected.length >= 5 || this.selected.includes(id)) return;
    this.selected = [...this.selected, id]; this.message = ''; this.errorMessage = '';
  }
  remove(id: number): void {
    this.selected = this.selected.filter(item => item !== id); this.message = '';
  }
  move(index: number, direction: -1 | 1): void {
    const next = index + direction;
    if (next < 0 || next >= this.selected.length) return;
    const reordered = [...this.selected];
    [reordered[index], reordered[next]] = [reordered[next], reordered[index]];
    this.selected = reordered; this.message = '';
  }
  save(): void {
    this.message = ''; this.errorMessage = '';
    if (!this.selected.length) { this.errorMessage = 'Select at least one project.'; return; }
    this.saving = true;
    this.api.submit(this.term, this.selected).subscribe({
      next: result => {
        this.submitted = result;
        this.saving = false;
        this.message = 'Your ranked preferences were saved successfully.';
      },
      error: error => {
        this.saving = false;
        this.errorMessage = error.status === 400 ? 'Please choose 1–5 different approved projects.' :
          'Unable to save preferences. Your previous saved ranking was not intentionally changed.';
      }
    });
  }
}
