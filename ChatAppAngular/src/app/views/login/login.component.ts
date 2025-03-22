import {Component} from '@angular/core';
import {AuthService} from "../../service/api/auth.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthReq} from "../../model/auth";
import {InputTextModule} from "primeng/inputtext";
import {FloatLabel} from "primeng/floatlabel";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {CommonModule, NgClass} from "@angular/common";
import {Router} from "@angular/router";
import {ToastService} from "../../service/utils/toast.service";

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, FormsModule, InputTextModule, FloatLabel,
    PasswordModule, ButtonModule, NgClass, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm!: FormGroup;
  loginMode = true;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private toastService: ToastService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  auth() {
    this.loginMode ? this.login() : this.register();
  }

  resetPassword() {
    this.router.navigate(['/reset-password']);
  }

  // TODO
  private login() {
    const authReq: AuthReq = this.loginForm.value;
    this.authService.login(authReq).subscribe({
      next: (val) => {
        this.toastService.showInfo('Zalogowano pomyślnie');
        this.router.navigate(['/home']);
      },
      error: (err) => {
        const errorResponse = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        if (errorResponse.validationErrors && errorResponse.validationErrors.length > 0) {
          const errorMessage = errorResponse.validationErrors.join(', ');
          this.toastService.showError(errorMessage);
        } else if (errorResponse.errorDescription) {
          this.toastService.showError(errorResponse.errorDescription);
        } else {
          this.toastService.showError(errorResponse.error);
        }
      }
    })
  }

  private register() {
    const authReq: AuthReq = this.loginForm.value;
    this.authService.register(authReq).subscribe({
      next: (val) => {
        if (val.status === 202) {
          this.toastService.showInfo('Na podany adres email został wysłany link aktywacyjny');
          this.loginMode = true;
        } else {
          this.toastService.showError('Wystąpił błąd podczas rejestracji, zgłoś problem do administratora');
        }
      },
      error: (err) => {
        const errorResponse = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        if (errorResponse.validationErrors && errorResponse.validationErrors.length > 0) {
          const errorMessage = errorResponse.validationErrors.join(', ');
          this.toastService.showError(errorMessage);
        } else if (errorResponse.errorDescription) {
          this.toastService.showError(errorResponse.errorDescription);
        } else {
          this.toastService.showError(errorResponse.error);
        }
      }
    })
  }
}
