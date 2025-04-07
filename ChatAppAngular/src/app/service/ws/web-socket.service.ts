import {Injectable} from '@angular/core';
import {TokenService} from "../utils/token.service";
import SockJS from "sockjs-client";
import * as Stomp from 'stompjs';

import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  stompClient: any;

  constructor(private tokenService: TokenService) {
  }

  connect() {
    const url = `${environment.apiUrl}/ws`;
    //const token = this.tokenService.token;
    const socket = new SockJS(url);
    this.stompClient = Stomp.over(socket);

    // const headers = {
    //   'Authorization': 'Bearer ' + token
    // };

    this.stompClient.connect(
      {},
      () => {
        console.log('connected to server');
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
