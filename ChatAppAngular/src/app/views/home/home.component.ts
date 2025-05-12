import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {WebSocketService} from "../../service/ws/web-socket.service";
import {AuthService} from "../../service/api/auth.service";
import {Subscription} from 'rxjs';
import {Notification} from '../../model/Notification';
import {UsersChatComponent} from "../../component/usersChat/users-chat.component";
import {ChatWindowComponent} from "../../component/chat-window/chat-window.component";
import {ChatResponse} from "../../model/ChatResponse";
import {DrawerModule} from "primeng/drawer";
import {ButtonModule} from "primeng/button";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-home',
  imports: [UsersChatComponent, ChatWindowComponent, DrawerModule, ButtonModule, CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  notifications =  signal<Notification | null>(null);
  notificationsSub: Subscription = new Subscription();
  selectedChat = signal<ChatResponse | null>(null);
  isMobile = false;
  sidebarVisible = false;

  constructor(
    private webSocketService: WebSocketService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize.bind(this));
    this.notificationsSub = this.webSocketService.getNotifications().subscribe((notification: Notification) => {
      this.notifications.set(notification);
      this.playNotificationSound();
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

  private checkScreenSize() {
    this.isMobile = window.innerWidth <= 768;
  }

  private playNotificationSound() {
    const audio = new Audio();
    audio.src = 'assets/sounds/newMsg.mp3';
    audio.load();
    audio.play().catch(err => {
      console.error('Błąd odtwarzania dźwięku:', err);
    });
  }
}
