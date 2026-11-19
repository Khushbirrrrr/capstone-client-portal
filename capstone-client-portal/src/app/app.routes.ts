import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { StudentDashboard } from './pages/student-dashboard/student-dashboard';
import { FacultyDashboard } from './pages/faculty-dashboard/faculty-dashboard';
import { Projects } from './pages/projects/projects';
import { ProjectDetail } from './pages/project-detail/project-detail';
import { Preferences } from './pages/preferences/preferences';
import { FacultyManagement } from './pages/faculty-management/faculty-management';
import { roleGuard } from './services/auth.guard';
import { MatchingPreviewPage } from './pages/matching-preview/matching-preview';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'student-dashboard', component: StudentDashboard, canActivate: [roleGuard('STUDENT')] },
  { path: 'faculty-dashboard', component: FacultyDashboard, canActivate: [roleGuard('FACULTY')] },
  { path: 'faculty-management', component: FacultyManagement, canActivate: [roleGuard('FACULTY')] },
  { path: 'matching-preview', component: MatchingPreviewPage, canActivate: [roleGuard('FACULTY')] },
  { path: 'projects', component: Projects, canActivate: [roleGuard('STUDENT')] },
  { path: 'projects/:id', component: ProjectDetail, canActivate: [roleGuard('STUDENT')] },
  { path: 'preferences', component: Preferences, canActivate: [roleGuard('STUDENT')] },
  { path: '**', redirectTo: 'login' }
];
