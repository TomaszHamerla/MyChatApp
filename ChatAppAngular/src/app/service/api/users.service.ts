import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {UserResponse} from "../../model/UserResponse";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(
    private http: HttpClient
  ) { }

  getUsers() {
    return this.http.get<UserResponse[]>(`${environment.apiUrl}/users`);
  }
}
