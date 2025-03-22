import {Routes} from '@angular/router';
import {ResetPasswordComponent} from "./views/reset-password/reset-password.component";
import {LoginComponent} from "./views/login/login.component";
import {ActivateAccountComponent} from "./views/activate-account/activate-account.component";

export const routes: Routes = [
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'reset-password', component: ResetPasswordComponent},
  {path: 'activate-account', component: ActivateAccountComponent},
];
