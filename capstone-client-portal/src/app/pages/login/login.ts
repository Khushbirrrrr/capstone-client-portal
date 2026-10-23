
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private auth = inject(Auth);
  private router = inject(Router);

  email = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  onLogin(): void {
    this.errorMessage = '';

    const email = this.email.trim().toLowerCase();

    if (!email.endsWith('@sheridancollege.ca')) {
      this.errorMessage = 'Please enter a valid Sheridan email address.';
      return;
    }

    if (!this.password) {
      this.errorMessage = 'Please enter your password.';
      return;
    }

    this.isLoading = true;

    this.auth.login(email, this.password).subscribe({
      next: (response) => {
        this.isLoading = false;

        if (response.role === 'STUDENT') {
          this.router.navigate(['/student-dashboard']);
        } else if (response.role === 'FACULTY') {
          this.router.navigate(['/faculty-dashboard']);
        } else {
          this.errorMessage = 'Unknown user role.';
        }
      },
      error: (error) => {
        this.isLoading = false;

        if (error.status === 401) {
          this.errorMessage = 'Invalid email or password.';
        } else {
          this.errorMessage =
            'Unable to connect to the server. Please try again.';
        }
      }
    });
  }
}
