import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CapstoneTerm, StudentGroup } from '../models/admin.model';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  getTerms(): Observable<CapstoneTerm[]> { return this.http.get<CapstoneTerm[]>('/api/admin/terms'); }
  createTerm(name: string): Observable<CapstoneTerm> { return this.http.post<CapstoneTerm>('/api/admin/terms', { name }); }
  activateTerm(id: number): Observable<CapstoneTerm> {
    return this.http.put<CapstoneTerm>(`/api/admin/terms/${id}/activate`, {});
  }
  getGroups(termId: number): Observable<StudentGroup[]> {
    return this.http.get<StudentGroup[]>('/api/admin/groups', { params: { termId } });
  }
  createGroup(name: string, termId: number, memberUsernames: string[]): Observable<StudentGroup> {
    return this.http.post<StudentGroup>('/api/admin/groups', { name, termId, memberUsernames });
  }
  deleteGroup(id: number): Observable<void> { return this.http.delete<void>(`/api/admin/groups/${id}`); }
}
