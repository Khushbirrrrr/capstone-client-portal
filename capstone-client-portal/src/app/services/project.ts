import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private http = inject(HttpClient);

  getApprovedProjects(term?: string): Observable<Project[]> {
    return this.http.get<Project[]>('/api/projects', {
      params: term ? new HttpParams().set('term', term) : undefined
    });
  }

  // US10: returns 404 when a project is unpublished or is from another term.
  getApprovedProject(id: number, term?: string): Observable<Project> {
    return this.http.get<Project>(`/api/projects/${id}`, {
      params: term ? new HttpParams().set('term', term) : undefined
    });
  }
}
