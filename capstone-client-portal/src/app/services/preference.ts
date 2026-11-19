import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RankedPreference } from '../models/preference.model';

@Injectable({ providedIn: 'root' })
export class PreferenceService {
  private http = inject(HttpClient);
  list(term: string): Observable<RankedPreference[]> {
    return this.http.get<RankedPreference[]>('/api/preferences', { params: { term } });
  }
  // PUT replaces only the signed-in student's ranking for the selected term.
  submit(term: string, projectIds: number[]): Observable<RankedPreference[]> {
    return this.http.put<RankedPreference[]>('/api/preferences', { term, projectIds });
  }
}
