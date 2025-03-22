import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../service/api/auth.service";

export const authGuard: CanActivateFn = (route, state) => {
  const isLoggedId = inject(AuthService).isLoggedId();
  return isLoggedId ? isLoggedId : inject(Router).createUrlTree(['/login']);
};
