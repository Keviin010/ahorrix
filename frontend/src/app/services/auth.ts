import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { API } from '../api';

export interface Usuario {
  id: string;
  nombre: string;
  email: string;
  rol: string;
  fechaRegistro?: string;
}

interface RespuestaSesion {
  usuario: Usuario;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  user = signal<Usuario | null>(this.leerUsuario());

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string) {
    return this.http.post<RespuestaSesion>(`${API}/auth/login`, { email, password })
      .pipe(tap(r => this.guardarSesion(r)));
  }

  register(nombre: string, email: string, password: string) {
    return this.http.post<RespuestaSesion>(`${API}/auth/register`, { nombre, email, password })
      .pipe(tap(r => this.guardarSesion(r)));
  }

  token(): string | null {
    return localStorage.getItem('monitor_token');
  }

  logout(): void {
    localStorage.removeItem('monitor_user');
    localStorage.removeItem('monitor_token');
    this.user.set(null);
    this.router.navigate(['/']);
  }

  isLogged(): boolean {
    return this.user() !== null;
  }

  private guardarSesion(respuesta: RespuestaSesion): void {
    localStorage.setItem('monitor_user', JSON.stringify(respuesta.usuario));
    localStorage.setItem('monitor_token', respuesta.token);
    this.user.set(respuesta.usuario);
  }

  private leerUsuario(): Usuario | null {
    try {
      if (!localStorage.getItem('monitor_token')) return null;
      return JSON.parse(localStorage.getItem('monitor_user') ?? 'null');
    } catch {
      return null;
    }
  }
}
