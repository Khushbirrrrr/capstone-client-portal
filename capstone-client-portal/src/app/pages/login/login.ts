import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login', standalone: true, imports: [FormsModule],
  templateUrl: './login.html', styleUrl: './login.scss'
})
export class Login {
  private auth = inject(Auth);
  private router = inject(Router);
  username = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  // Invalid or empty fields should not enable the submit action.
  get canSignIn(): boolean {
    return !this.isLoading
      && /^[a-z][a-z0-9._-]{2,39}$/.test(this.username.trim().toLowerCase())
      && this.password.length > 0;
  }

  onLogin(): void {
    this.errorMessage = '';
    const username = this.username.trim().toLowerCase();
    // Explicitly reject school addresses to avoid confusion with Sheridan SSO.
    if (!/^[a-z][a-z0-9._-]{2,39}$/.test(username)) {
      this.errorMessage = 'Enter your Capstone Portal username, not a Sheridan email.';
      return;
    }
    if (!this.password) {
      this.errorMessage = 'Please enter your portal password.';
      return;
    }
    this.isLoading = true;
    this.auth.login(username, this.password).subscribe({
      next: response => {
        this.isLoading = false;
        this.password = '';
        void this.router.navigate([response.role === 'FACULTY' ? '/faculty-dashboard' : '/student-dashboard']);
      },
      error: error => {
        this.isLoading = false;
        this.password = '';
        this.errorMessage = error.status === 401 ? 'Invalid portal username or password.' :
          error.status === 400 ? 'Use a portal username, not an email address.' :
          'Could not sign in. Make sure the backend is running and try again.';
      }
    });
  }
}
