import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap, map } from 'rxjs';

export interface LoginResponse {
  username: string;
  role: 'STUDENT' | 'FACULTY';
}

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);

  // Spring creates the anti-CSRF token before the first state-changing request.
  refreshCsrf(): Observable<{ token: string }> {
    return this.http.get<{ token: string }>('/api/auth/csrf', { withCredentials: true });
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.refreshCsrf().pipe(
      switchMap(() => this.http.post<LoginResponse>('/api/auth/login',
        { username, password }, { withCredentials: true })),
      // Refresh the browser CSRF cookie after login before PUT/POST/logout.
      switchMap(user => this.refreshCsrf().pipe(map(() => user)))
    );
  }

  me(): Observable<LoginResponse> {
    return this.http.get<LoginResponse>('/api/auth/me', { withCredentials: true });
  }

  logout(): Observable<unknown> {
    return this.http.post('/api/auth/logout', {}, { withCredentials: true });
  }
}
