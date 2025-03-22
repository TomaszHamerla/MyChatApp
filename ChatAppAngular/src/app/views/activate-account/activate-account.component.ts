import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {InputOtp} from "primeng/inputotp";
import {ButtonModule} from "primeng/button";
import {AuthService} from "../../service/api/auth.service";
import {ToastService} from "../../service/utils/toast.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-activate-account',
  imports: [FormsModule, InputOtp, ButtonModule],
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css'
})
export class ActivateAccountComponent {
  token!: string

  constructor(
    private authService: AuthService,
    private router: Router,
    private toastService: ToastService
  ) {
  }

  activateAccount() {
    this.authService.activateAccount(this.token).subscribe({
      next: () => {
        this.toastService.showInfo('Konto zostało aktywowane');
        this.redirectToLogin();
      },
      error: (err) => {
        const errorResponse = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastService.showError(errorResponse.error);
      }
    });
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }
}
