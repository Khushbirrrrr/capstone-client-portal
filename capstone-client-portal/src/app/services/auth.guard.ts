import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { Auth, LoginResponse } from './auth';

/** UI guard for navigation; the backend enforces roles independently. */
export const roleGuard = (role: LoginResponse['role']): CanActivateFn => () => {
  const auth = inject(Auth);
  const router = inject(Router);
  return auth.me().pipe(
    map(user => user.role === role ? true : router.parseUrl('/login')),
    catchError(() => of(router.parseUrl('/login')))
  );
};
