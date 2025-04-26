import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {ChatResponse} from "../../model/ChatResponse";

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  constructor(
    private http: HttpClient
  ) {
  }

  getChatList() {
    return this.http.get<ChatResponse[]>(`${environment.apiUrl}/chats`);
  }
  createChat(receiverId: number) {
    const params = new HttpParams()
      .set('receiverId', receiverId);
    return this.http.post<number>(`${environment.apiUrl}/chats`, null, {params});
  }
}
