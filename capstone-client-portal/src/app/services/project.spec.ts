import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProjectService } from './project';

describe('ProjectService', () => {
  let api: ProjectService;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(ProjectService);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());
  it('requests approved projects for the selected term', () => {
    api.getApprovedProjects('Term A').subscribe(result => expect(result.length).toBe(1));
    const req = http.expectOne(r => r.url === '/api/projects' && r.params.get('term') === 'Term A');
    req.flush([{ id: 1, title: 'Example', description: 'Example description', status: 'APPROVED', term: 'Term A' }]);
  });
  it('requests project details by ID', () => {
    api.getApprovedProject(12, 'Term B').subscribe(result => expect(result.id).toBe(12));
    http.expectOne(r => r.url === '/api/projects/12' && r.params.get('term') === 'Term B')
      .flush({ id: 12, title: 'Example', description: '', status: 'APPROVED', term: 'Term B' });
  });
});
