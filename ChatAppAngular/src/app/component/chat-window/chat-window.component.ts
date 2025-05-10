import {Component, effect, Input, Signal} from '@angular/core';
import {ChatResponse} from "../../model/ChatResponse";
import {CommonModule} from "@angular/common";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {ChatService} from "../../service/api/chat.service";

@Component({
  selector: 'app-chat-window',
  imports: [CommonModule, InputTextModule, FormsModule, ButtonModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent {
  @Input({required: true})
  selectedChat!: Signal<ChatResponse | null>;
  text = '';
  messages: string[] = [];

  constructor(
    private chatService: ChatService
  ) {
    effect(() => {
      const chat = this.selectedChat();
      if (chat) {
        console.log(chat)
      } else {
        console.log('No chat selected');
      }
    });
  }
}
