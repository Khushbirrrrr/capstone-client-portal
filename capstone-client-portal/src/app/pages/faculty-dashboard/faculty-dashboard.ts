import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({ selector: 'app-faculty-dashboard', standalone: true, imports: [RouterLink],
  templateUrl: './faculty-dashboard.html', styleUrl: './faculty-dashboard.scss' })
export class FacultyDashboard {
  private auth = inject(Auth);
  private router = inject(Router);
  logout(): void { this.auth.logout().subscribe({ next: () => void this.router.navigate(['/login']) }); }
}
