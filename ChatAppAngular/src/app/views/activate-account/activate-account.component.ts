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

  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }
}
