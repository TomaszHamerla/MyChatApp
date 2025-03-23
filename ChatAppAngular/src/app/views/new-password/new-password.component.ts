import { Component } from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../service/api/auth.service";
import {Router} from "@angular/router";
import {ToastService} from "../../service/utils/toast.service";
import {InputTextModule} from "primeng/inputtext";
import {FloatLabel} from "primeng/floatlabel";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {CommonModule, NgClass} from "@angular/common";

@Component({
  selector: 'app-new-password',
  imports: [ReactiveFormsModule, FormsModule, InputTextModule, FloatLabel,
    PasswordModule, ButtonModule, CommonModule],
  templateUrl: './new-password.component.html',
  styleUrl: './new-password.component.css'
})
export class NewPasswordComponent {
  restartForm!: FormGroup;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router,
    private toastService: ToastService
  ) {
    this.restartForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  restart() {
    this.authService.resetPassword(this.restartForm.value).subscribe({
      next: () => {
        this.toastService.showInfo('Hasło zostało zresetowane');
        this.router.navigate(['/login']);
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
    });
  }
}
