import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Toast} from "primeng/toast";
import {MenuComponent} from "./views/menu/menu.component";
import {CommonModule} from "@angular/common";
import {AuthService} from "./service/api/auth.service";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast, MenuComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

  constructor(
    private authService: AuthService
  ) {
  }

  isLoggedIn() {
    return this.authService.isLoggedId();
  }
}
