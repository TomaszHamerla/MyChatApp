import {Component, OnInit} from '@angular/core';
import {Menubar} from "primeng/menubar";
import {MenuItem} from "primeng/api";
import {AuthService} from "../../service/api/auth.service";
import {ButtonModule} from "primeng/button";

@Component({
  selector: 'app-menu',
  imports: [Menubar, ButtonModule],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit {
  items: MenuItem[] | undefined;

  constructor(
    private authService: AuthService
  ) {
  }


  ngOnInit() {
    this.items = [
      {
        label: 'Strona główna',
        icon: 'pi pi-home',
        routerLink: ['home']
      }];
  }

  logout() {
    this.authService.logout();
  }
}
