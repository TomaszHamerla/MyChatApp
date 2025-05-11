import {Component, effect, ElementRef, Input, Signal, ViewChild} from '@angular/core';
import {ChatResponse} from "../../model/ChatResponse";
import {CommonModule} from "@angular/common";
import {InputTextModule} from "primeng/inputtext";
import {FormsModule} from "@angular/forms";
import {ButtonModule} from "primeng/button";
import {ChatService} from "../../service/api/chat.service";
import {MessageRequest} from "../../model/MessageRequest";
import {MessageResponse} from "../../model/MessageResponse";
import {ToastService} from "../../service/utils/toast.service";
import {Notification} from "../../model/Notification";

@Component({
  selector: 'app-chat-window',
  imports: [CommonModule, InputTextModule, FormsModule, ButtonModule],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent {
  @Input({required: true})
  selectedChat!: Signal<ChatResponse | null>;
  @Input({required: true})
  notifications!: Signal<Notification | null>;
  text = '';
  messages: MessageResponse[] = [];
  @ViewChild('scrollableDiv')
  scrollableDiv!: ElementRef<HTMLDivElement>;
  senderId: number;

  constructor(
    private chatService: ChatService,
    private toastService: ToastService
  ) {
    const storedId = localStorage.getItem('senderId');
    this.senderId = storedId ? +storedId : 0;
    effect(() => {
      const chat = this.selectedChat();
      if (chat) {
        this.getMessages(chat.id);
      } else {
        console.log('No chat selected');
      }
    });

    effect(() => {
      const notification = this.notifications();
      const chat = this.selectedChat();
      if (notification && chat) {
        if (notification.chatId === chat.id) {
          const newMessage: MessageResponse = {
            id: 0,
            content: notification.content ?? '',
            senderId: notification.senderId ?? 0,
            receiverId: notification.receiverId ?? 0
          }
          this.messages.push(newMessage);
          this.scrollToBottom();
        }
      }
    });
  }

  sendMsg() {
    const chat = this.selectedChat();
    if (this.text.trim() !== '' && chat) {
      const messageRequest: MessageRequest = {
        content: this.text,
        senderId: this.getSenderId(chat),
        receiverId: this.getReceiverId(chat),
        chatId: chat.id
      };
      this.chatService.sendMessage(messageRequest).subscribe({
        next: () => {
          this.messages.push({
            content: this.text,
            senderId: messageRequest.senderId,
            receiverId: messageRequest.receiverId
          });
          this.text = '';
        },
        error: (error) => {
          this.toastService.showError(error.error.message);
        },
        complete: () => {
          this.scrollToBottom();
        }
      });
    }
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMsg();
    }
  }

  private getReceiverId(chat: ChatResponse) {
    if (chat.senderId === this.senderId) {
      return chat.receiverId;
    }
    return chat.senderId;
  }

  private getSenderId(chat: ChatResponse) {
    if (chat.senderId === this.senderId) {
      return chat.senderId;
    }
    return chat.receiverId;
  }

  private getMessages(id: number) {
    this.chatService.getMessages(id).subscribe({
      next: (response) => {
        this.messages = response;
      },
      error: (error) => {
        this.toastService.showError(error.error.message);
      },
      complete: () => {
        this.scrollToBottom();
      }
    });
  }

  private scrollToBottom(): void {
    if (this.scrollableDiv) {
      setTimeout(() => {
        const div = this.scrollableDiv.nativeElement;
        div.scrollTo({
          top: div.scrollHeight,
          behavior: 'smooth'
        });
      }, 200);
    }
  }
}
