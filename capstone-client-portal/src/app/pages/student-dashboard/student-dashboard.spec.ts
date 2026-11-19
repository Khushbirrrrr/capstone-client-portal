import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { StudentDashboard } from './student-dashboard';

describe('StudentDashboard', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentDashboard], providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
  });
  it('creates the page', () => {
    const fixture = TestBed.createComponent(StudentDashboard);
    fixture.detectChanges();
    expect(fixture.componentInstance).toBeTruthy();
  });
});
