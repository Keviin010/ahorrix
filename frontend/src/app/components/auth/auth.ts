import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { EscenaComponent } from '../escena/escena';

@Component({
  selector: 'app-auth',
  imports: [FormsModule, RouterLink, EscenaComponent],
  templateUrl: './auth.html',
  styleUrl: './auth.css'
})
export class AuthComponent implements OnInit {
  modo = signal<'login' | 'registro'>('login');
  cargando = signal(false);
  error = signal('');

  nombre = '';
  email = '';
  password = '';
  confirmacion = '';

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (this.auth.isLogged()) {
      this.router.navigate(['/dashboard']);
    }
  }

  cambiarModo(modo: 'login' | 'registro'): void {
    this.modo.set(modo);
    this.error.set('');
  }

  enviar(): void {
    this.error.set('');
    const registro = this.modo() === 'registro';

    if (!this.email.trim() || !this.password.trim()) {
      this.error.set('Completa correo y contraseña.');
      return;
    }
    if (registro && !this.nombre.trim()) {
      this.error.set('Escribe tu nombre completo.');
      return;
    }
    if (registro && this.password !== this.confirmacion) {
      this.error.set('Las contraseñas no coinciden.');
      return;
    }

    this.cargando.set(true);
    const peticion = registro
      ? this.auth.register(this.nombre.trim(), this.email.trim(), this.password)
      : this.auth.login(this.email.trim(), this.password);

    peticion.subscribe({
      next: () => {
        this.cargando.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.cargando.set(false);
        this.error.set(err.error?.mensaje || 'No pudimos completar la operación.');
      }
    });
  }
}
