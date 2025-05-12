import { Injectable } from '@angular/core';
import { TokenService } from "../utils/token.service";
import SockJS from "sockjs-client";
import { Client, IMessage } from '@stomp/stompjs';
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
    const socket = new SockJS(url);

    this.stompClient = new Client({
      webSocketFactory: () => socket,
      debug: () => {},
      onConnect: () => {
        this.notificationsSubscription = this.stompClient.subscribe(`/user/${userEmail}/chat`, (message: IMessage) => {
          if (message.body) {
            const notification: Notification = JSON.parse(message.body);
            this.notificationsSubject.next(notification);
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error', frame);
      }
    });

    this.stompClient.activate();
  }

  disconnect(): void {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
    }
  }
}
