import { Component } from '@angular/core';
import {FormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {FloatLabel} from "primeng/floatlabel";
import {ButtonModule} from "primeng/button";
import {Router} from "@angular/router";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-reset-password',
  imports: [FormsModule, InputTextModule, FloatLabel, ButtonModule, CommonModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  email!: string;

  constructor(
    private route: Router
  ) {
  }
  cancel() {
    this.route.navigate(['/login']);
  }
}
