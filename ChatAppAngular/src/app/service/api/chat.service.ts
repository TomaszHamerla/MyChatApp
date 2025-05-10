import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {ChatResponse} from "../../model/ChatResponse";
import {MessageResponse} from "../../model/MessageResponse";
import {MessageRequest} from "../../model/MessageRequest";

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

  getMessages(chatId: number) {
    return this.http.get<MessageResponse[]>(`${environment.apiUrl}/messages/chat/${chatId}`);
  }

  sendMessage(messageRequest: MessageRequest) {
    return this.http.post<MessageRequest>(`${environment.apiUrl}/messages`, messageRequest);
  }
}
