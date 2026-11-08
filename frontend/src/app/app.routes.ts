import { Routes } from '@angular/router';
import { AuthComponent } from './components/auth/auth';
import { ProductosComponent } from './components/productos/productos';
import { sesionGuard } from './guards';

export const routes: Routes = [
  { path: '', redirectTo: 'entrar', pathMatch: 'full' },
  { path: 'entrar', component: AuthComponent, title: 'Entrar - Ahorrix' },
  { path: 'dashboard', component: ProductosComponent, canActivate: [sesionGuard], title: 'Mis productos - Ahorrix' },
  { path: '**', redirectTo: 'entrar' }
];
