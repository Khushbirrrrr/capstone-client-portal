import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { FacultyManagement } from './faculty-management';

describe('FacultyManagement: group input limits', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FacultyManagement],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
  });

  it('does not allow duplicate usernames', () => {
    const component = TestBed.createComponent(FacultyManagement).componentInstance;
    component.memberInput = 'Student1';
    component.addMember();
    component.memberInput = 'student1';
    expect(component.canAddMember).toBeFalse();
    component.addMember();
    expect(component.memberUsernames).toEqual(['student1']);
  });

  it('disables adding a fifth member', () => {
    const component = TestBed.createComponent(FacultyManagement).componentInstance;
    for (let index = 1; index <= 4; index++) {
      component.memberInput = `student${index}`;
      component.addMember();
    }
    component.memberInput = 'student5';
    expect(component.canAddMember).toBeFalse();
    component.addMember();
    expect(component.memberUsernames.length).toBe(4);
  });

  it('only enables group creation with a valid name and members', () => {
    const component = TestBed.createComponent(FacultyManagement).componentInstance;
    component.selectedTermId = 1;
    component.groupName = 'Group A';
    expect(component.canCreateGroup).toBeFalse();
    component.memberInput = 'student1';
    component.addMember();
    expect(component.canCreateGroup).toBeTrue();
  });
});
