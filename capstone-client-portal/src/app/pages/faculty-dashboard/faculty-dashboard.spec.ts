import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FacultyDashboard } from './faculty-dashboard';

describe('FacultyDashboard', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacultyDashboard], providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
  });
  it('creates the page', () => {
    const fixture = TestBed.createComponent(FacultyDashboard);
    fixture.detectChanges();
    expect(fixture.componentInstance).toBeTruthy();
  });
});
