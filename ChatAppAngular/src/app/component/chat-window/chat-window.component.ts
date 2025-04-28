import {Component, effect, Input, Signal} from '@angular/core';
import {ChatResponse} from "../../model/ChatResponse";
import {CommonModule} from "@angular/common";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";

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
