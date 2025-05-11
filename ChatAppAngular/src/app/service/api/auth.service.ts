import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthReq, AuthResponse} from "../../model/auth";
import {environment} from "../../../environments/environment";
import {TokenService} from "../utils/token.service";
import {tap} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
  }

  get userEmail(): string {
    return localStorage.getItem('userEmail') as string;
  }

  login(authReq: AuthReq) {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, authReq).pipe(
      tap((authRes: AuthResponse) => {
          this.tokenService.token = authRes.token;
          localStorage.setItem('senderId', authRes.id.toString());
          localStorage.setItem('userEmail', authReq.email);
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
    localStorage.removeItem('userEmail');
    localStorage.removeItem('senderId');
    this.router.navigate(['login']);
  }

  isLoggedId() {
    return this.tokenService.token !== null;
  }
}
