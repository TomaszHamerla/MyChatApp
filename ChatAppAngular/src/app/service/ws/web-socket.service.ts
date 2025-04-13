import { Injectable } from '@angular/core';
import { TokenService } from "../utils/token.service";
import SockJS from "sockjs-client";
import * as Stomp from 'stompjs';
import { environment } from "../../../environments/environment";
import { Subscription, Subject, Observable } from 'rxjs';
import { Notification } from "../../model/Notification";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  stompClient: any;
  notificationsSubscription: Subscription = new Subscription();

  private notificationsSubject = new Subject<Notification>();

  constructor(private tokenService: TokenService) { }

  getNotifications(): Observable<Notification> {
    return this.notificationsSubject.asObservable();
  }

  connect(userEmail: string): void {
    const url = `${environment.apiUrl}/ws`;
    // const token = this.tokenService.token;
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);

    // Możesz ustawić nagłówki, jeśli są wymagane
    // const headers = {
    //   'Authorization': 'Bearer ' + token
    // };

    this.stompClient.connect(
      {},
      () => {
        // Subskrybuj kanał i emituj powiadomienia do subject
        this.notificationsSubscription = this.stompClient.subscribe(`/user/${userEmail}/chat`, (message: any) => {
          if (message.body) {
            const notification: Notification = JSON.parse(message.body);
            console.log('Odebrano wiadomość:', notification);
            // Emituj powiadomienie
            this.notificationsSubject.next(notification);
          }
        });
      },
      (error: any) => {
        console.error('Błąd połączenia WebSocket:', error);
      }
    );
  }

  disconnect(): void {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect(() => {
        console.log('Rozłączono WebSocket');
      });
    }
  }
}
