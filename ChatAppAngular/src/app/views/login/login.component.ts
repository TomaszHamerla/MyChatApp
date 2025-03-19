import {Component} from '@angular/core';
import {AuthService} from "../../service/api/auth.service";
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthReq} from "../../model/auth";
import {InputTextModule} from "primeng/inputtext";
import {FloatLabel} from "primeng/floatlabel";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {NgClass} from "@angular/common";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule,FormsModule, InputTextModule, FloatLabel, PasswordModule, ButtonModule, NgClass],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginForm!: FormGroup;
  loginMode = true;

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  login() {
    const authReq: AuthReq = this.loginForm.value;
    this.authService.login(authReq).subscribe({
      next: (val) =>
        console.log(val)
    });
  }

  resetPassword() {
    this.router.navigate(['/reset-password']);
  }
}
