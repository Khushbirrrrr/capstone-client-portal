import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { StudentDashboard } from './pages/student-dashboard/student-dashboard';
import { FacultyDashboard } from './pages/faculty-dashboard/faculty-dashboard';
import { Projects } from './pages/projects/projects';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'student-dashboard',
    component: StudentDashboard
  },
  {
    path: 'faculty-dashboard',
    component: FacultyDashboard
  },
  {
  path: 'projects',
  component: Projects
},
  {
    path: '**',
    redirectTo: 'login'
  }
];
