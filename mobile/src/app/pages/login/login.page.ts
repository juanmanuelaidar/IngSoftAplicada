import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
})
export class LoginPage {
  username = 'admin';
  password = 'admin';
  loading = false;
  errorMessage = '';

  constructor(
    private readonly api: ApiService,
    private readonly router: Router,
  ) {}

  login(): void {
    if (!this.username.trim() || !this.password) {
      this.errorMessage = 'Ingresa usuario y contrasena.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.api
      .login(this.username.trim(), this.password)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => this.router.navigate(['/products']),
        error: () => {
          this.errorMessage = 'Usuario o contrasena invalidos.';
        },
      });
  }
}
