import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-nav',
  imports: [RouterLink],
  templateUrl: './nav.html',
  styleUrl: './nav.css'
})
export class NavComponent {
  constructor(public auth: AuthService) {}

  inicial(): string {
    return (this.auth.user()?.nombre ?? '?').charAt(0).toUpperCase();
  }
}
