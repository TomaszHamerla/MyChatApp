import {Component, effect, Input, Signal} from '@angular/core';
import {ChatResponse} from "../../model/ChatResponse";

@Component({
  selector: 'app-chat-window',
  imports: [],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent {
  @Input({required: true})
  selectedChat!: Signal<ChatResponse | null>;

  constructor() {
    effect(() => {
      const chat = this.selectedChat();
      if (chat) {
        console.log(chat)
      } else {
      }
    });
  }
}
