import { Component, OnDestroy, OnInit } from '@angular/core';
import { WebSocketService } from "../../service/ws/web-socket.service";
import { AuthService } from "../../service/api/auth.service";
import { Subscription } from 'rxjs';
import { Notification } from '../../model/Notification';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  notificationsSub: Subscription = new Subscription();

  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.notificationsSub = this.webSocketService.getNotifications().subscribe((notification: Notification) => {
      this.notifications.push(notification);
      console.log('Nowe powiadomienie w komponencie:', notification);
    });

    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.notificationsSub.unsubscribe();
    this.webSocketService.disconnect();
  }

  private connectWebSocket() {
    const userEmail = this.authService.userEmail;
    this.webSocketService.connect(userEmail);
  }
}
