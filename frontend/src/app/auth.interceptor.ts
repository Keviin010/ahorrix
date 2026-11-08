import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './services/auth';
import { API } from './api';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const peticion = token && req.url.startsWith(API)
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError(error => {
      const esLogin = req.url.startsWith(`${API}/auth/`);
      if (error.status === 401 && !esLogin) {
        auth.logout();
      }
      return throwError(() => error);
    })
  );
};
