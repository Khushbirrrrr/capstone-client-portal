import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { PreferenceService } from './preference';

describe('PreferenceService', () => {
  let api: PreferenceService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(PreferenceService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('sends ordered project IDs when saving a ranking', () => {
    api.submit('Term A', [5, 3, 2]).subscribe(result => expect(result.length).toBe(3));
    const req = http.expectOne('/api/preferences');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ term: 'Term A', projectIds: [5, 3, 2] });
    req.flush([5, 3, 2].map((id, i) => ({ projectId: id, projectTitle: 'Test', rank: i + 1, submittedAt: '2026-01-01T00:00:00Z' })));
  });
});
