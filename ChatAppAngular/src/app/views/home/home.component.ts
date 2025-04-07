import {Component, OnDestroy, OnInit} from '@angular/core';
import {WebSocketService} from "../../service/ws/web-socket.service";

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit, OnDestroy {

  constructor(
    private webSocketService: WebSocketService
  ) {
  }

  ngOnInit(): void {
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  private connectWebSocket() {
    this.webSocketService.connect();
  }
}
