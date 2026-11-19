import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({ selector: 'app-student-dashboard', standalone: true, imports: [RouterLink],
  templateUrl: './student-dashboard.html', styleUrl: './student-dashboard.scss' })
export class StudentDashboard {
  private auth = inject(Auth);
  private router = inject(Router);
  logout(): void { this.auth.logout().subscribe({ next: () => void this.router.navigate(['/login']) }); }
}
