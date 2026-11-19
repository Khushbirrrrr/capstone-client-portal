import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchingPreview } from '../models/matching.model';

@Injectable({ providedIn: 'root' })
export class MatchingService {
  private http = inject(HttpClient);
  generate(termId: number): Observable<MatchingPreview> {
    return this.http.post<MatchingPreview>('/api/matching/generate', { termId });
  }
}
