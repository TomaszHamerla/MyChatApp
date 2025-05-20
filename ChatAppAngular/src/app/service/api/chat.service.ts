import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {ChatResponse} from "../../model/ChatResponse";
import {MessageResponse} from "../../model/MessageResponse";
import {MessageRequest} from "../../model/MessageRequest";
import {Page} from "../../model/Page";
import {UserResponse} from "../../model/UserResponse";

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

  getMessages(chatId: number, page: number, size: number = 20) {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<MessageResponse>>(`${environment.apiUrl}/messages/chat/${chatId}`, {params});
  }

  sendMessage(messageRequest: MessageRequest) {
    return this.http.post<void>(`${environment.apiUrl}/messages`, messageRequest);
  }

  setMessageToSeen(chatId: number) {
    return this.http.patch<void>(`${environment.apiUrl}/messages/chat/${chatId}`, null);
  }

  updateNickName(userId: number, chatId: number, newNick: string) {
    const params = new HttpParams()
      .set('userId', userId)
      .set('chatId', chatId)
      .set('newNick', newNick);
    return this.http.patch<UserResponse>(`${environment.apiUrl}/chats/updateUserNick`, null, {params});
  }

  uploadFile(file: File, messageRequest: MessageRequest) {
    const msgRequest = JSON.stringify(messageRequest);
    const formData = new FormData();
    formData.append('file', file);
    formData.append('messageRequest', msgRequest);
    return this.http.post<MessageResponse>(`${environment.apiUrl}/messages/upload`, formData);
  }
}
