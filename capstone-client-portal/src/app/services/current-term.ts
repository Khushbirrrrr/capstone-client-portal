import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CurrentTermService {
  private http = inject(HttpClient);
  get(): Observable<{ name: string }> { return this.http.get<{ name: string }>('/api/terms/current'); }
}
