import {Routes} from '@angular/router';
import {ResetPasswordComponent} from "./views/reset-password/reset-password.component";
import {LoginComponent} from "./views/login/login.component";

export const routes: Routes = [
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'reset-password', component: ResetPasswordComponent}
];
