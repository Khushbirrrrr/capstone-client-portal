import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MatchingPreview, AllocationOption, AllocationChoice, FinalAllocation } from '../models/matching.model';

@Injectable({ providedIn: 'root' })
export class MatchingService {
  private http = inject(HttpClient);
  options(termId: number) { return this.http.get<AllocationOption[]>(`/api/matching/final/${termId}/options`); }
  final(termId: number) { return this.http.get<FinalAllocation[]>(`/api/matching/final/${termId}`); }
  confirm(baseline: MatchingPreview, choices: AllocationChoice[]) { return this.http.post<FinalAllocation[]>(`/api/matching/final/${baseline.termId}`, {baseline, choices}); }
  generate(termId: number): Observable<MatchingPreview> {
    return this.http.post<MatchingPreview>('/api/matching/generate', { termId });
  }
}
