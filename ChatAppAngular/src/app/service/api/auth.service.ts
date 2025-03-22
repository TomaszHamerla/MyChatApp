import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthReq} from "../../model/auth";
import {environment} from "../../../environments/environment";
import {TokenService} from "../utils/token.service";
import {tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private http: HttpClient,
    private tokenService: TokenService
  ) {
  }

  login(authReq: AuthReq) {
   return this.http.post<string>(`${environment.apiUrl}/auth/login`, authReq, {responseType: 'text' as 'json'}).pipe(
      tap(token => {
          this.tokenService.token = token;
        }
      ));
  }
}
