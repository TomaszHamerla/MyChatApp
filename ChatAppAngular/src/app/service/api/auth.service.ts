import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthReq} from "../../model/auth";
import {environment} from "../../../environments/environment";
import {TokenService} from "../utils/token.service";
import {tap} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  userEmail: string = '';

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
  }

  login(authReq: AuthReq) {
    return this.http.post<string>(`${environment.apiUrl}/auth/login`, authReq, {responseType: 'text' as 'json'}).pipe(
      tap(token => {
          this.tokenService.token = token;
          this.userEmail = authReq.email;
        }
      ));
  }

  register(authReq: AuthReq) {
    return this.http.post<void>(`${environment.apiUrl}/auth/register`, authReq, {observe: 'response'});
  }

  activateAccount(token: string) {
    const params = {token};
    return this.http.post<void>(`${environment.apiUrl}/auth/activate-account`, {}, {params});
  }

  sendResetPasswordLink(email: string) {
    const params = {email};
    return this.http.post<void>(`${environment.apiUrl}/auth/send-reset-password-link`, {}, {params});
  }

  resetPassword(authReq: AuthReq) {
    return this.http.post<void>(`${environment.apiUrl}/auth/reset-password`, authReq);
  }

  logout() {
    this.tokenService.removeToken();
    this.router.navigate(['login']);
  }

  isLoggedId() {
    return this.tokenService.token !== null;
  }
}
