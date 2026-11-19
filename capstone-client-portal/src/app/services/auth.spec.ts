import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Auth } from './auth';

describe('Auth', () => {
  let api: Auth;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(Auth);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('gets an anti-CSRF token, signs in with a username and refreshes the token', () => {
    let role = '';
    const username = 'test-' + Math.random().toString(36).slice(2, 10);
    const password = Math.random().toString(36) + Math.random().toString(36);
    api.login(username, password).subscribe(user => role = user.role);
    http.expectOne('/api/auth/csrf').flush({ token: 'test-token' });
    const request = http.expectOne('/api/auth/login');
    expect(request.request.body.username).toBe(username);
    expect(request.request.body.email).toBeUndefined();
    request.flush({ username: 'test-portal', role: 'STUDENT' });
    http.expectOne('/api/auth/csrf').flush({ token: 'another-test-token' });
    expect(role).toBe('STUDENT');
  });
  it('reads current authenticated identity', () => {
    api.me().subscribe(user => expect(user.role).toBe('FACULTY'));
    http.expectOne('/api/auth/me').flush({ username: 'faculty-test', role: 'FACULTY' });
  });
});
