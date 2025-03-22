import {Routes} from '@angular/router';
import {ResetPasswordComponent} from "./views/reset-password/reset-password.component";
import {LoginComponent} from "./views/login/login.component";
import {ActivateAccountComponent} from "./views/activate-account/activate-account.component";
import {HomeComponent} from "./views/home/home.component";
import {authGuard} from "./guards/auth.guard";
import {redirectGuard} from "./guards/redirect.guard";

export const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent, canActivate: [redirectGuard]},
  {path: 'reset-password', component: ResetPasswordComponent, canActivate: [redirectGuard]},
  {path: 'activate-account', component: ActivateAccountComponent, canActivate: [redirectGuard]},
  {path: 'home', component: HomeComponent, canActivate: [authGuard]},
  {path: '**', redirectTo: '/home'},
];
