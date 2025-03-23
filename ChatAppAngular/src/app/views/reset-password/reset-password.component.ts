import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {FloatLabel} from "primeng/floatlabel";
import {ButtonModule} from "primeng/button";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";
import {AuthService} from "../../service/api/auth.service";
import {ToastService} from "../../service/utils/toast.service";

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule, InputTextModule, FloatLabel, ButtonModule, CommonModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  email!: string;

  constructor(
    private route: Router,
    private auth: AuthService,
    private toast: ToastService
  ) {
  }

  sendEmail() {
    this.auth.sendResetPasswordLink(this.email).subscribe({
      next: () => {
        this.toast.showInfo('Na podany adres email został wysłany link do zresetowania hasła');
        this.route.navigate(['/login']);
      },
      error: (err) => {
        const errorResponse = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        if (errorResponse.errorDescription) {
          this.toast.showError(errorResponse.errorDescription);
        } else {
          this.toast.showError(errorResponse.error);
        }
      }
    });
  }

  cancel() {
    this.route.navigate(['/login']);
  }
}
