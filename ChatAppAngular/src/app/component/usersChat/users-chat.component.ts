import {Component, effect, Input, OnInit, output, Signal} from '@angular/core';
import {UsersService} from "../../service/api/users.service";
import {UserResponse} from "../../model/UserResponse";
import {ToastService} from "../../service/utils/toast.service";
import {FormsModule} from "@angular/forms";
import {Listbox} from "primeng/listbox";
import {Select} from "primeng/select";
import {ChatService} from "../../service/api/chat.service";
import {ChatResponse} from "../../model/ChatResponse";
import {CommonModule} from "@angular/common";
import {BadgeModule} from "primeng/badge";
import {Notification} from "../../model/Notification";

@Component({
  selector: 'app-users-chat',
  imports: [FormsModule, Listbox, Select, CommonModule, BadgeModule],
  templateUrl: './users-chat.component.html',
  styleUrl: './users-chat.component.css'
})
export class UsersChatComponent implements OnInit {
  users: UserResponse[] = [];
  chats: ChatResponse[] = [];
  chatSelected = output<ChatResponse | null>();
  selectedChat: ChatResponse | null = null;
  @Input({required: true})
  notifications!: Signal<Notification | null>;

  constructor(
    private usersService: UsersService,
    private toastService: ToastService,
    private chatService: ChatService
  ) {
    effect(() => {
      const notification = this.notifications();
      if (notification) {
        const chat = this.chats.find(c => c.id === notification.chatId);
        if (chat) {
          chat.unreadMessages++;
        }
      }
    });
  }

  ngOnInit(): void {
    this.getUsers();
    this.refreshChatList();
  }

  handleUserSelect(user: UserResponse, sel: Select) {
    if (!user) return;
    this.createChat(user);
    sel.clear();
  }

  onChange(chat: ChatResponse | null) {
    this.chatSelected.emit(chat);
  }

  private getUsers() {
    this.usersService.getUsers().subscribe({
      next: (response) => {
        this.users = response;
      },
      error: (error) => {
        this.toastService.showError(error.error.message);
      }
    })
  }

  private refreshChatList(chatId?: number) {
    this.chatService.getChatList().subscribe({
      next: (list) => {
        this.chats = list;

        if (chatId) {
          const chat = this.chats.find(c => c.id === chatId) ?? null;
          this.selectedChat = chat;
          this.chatSelected.emit(chat);
        }
      },
      error: (e) => this.toastService.showError(e.error.message),
    });
  }

  private createChat(user: UserResponse) {
    this.chatService.createChat(user.id).subscribe({
      next: (chatId) => {
        this.refreshChatList(chatId);
      },
      error: (error) => {
        this.toastService.showError(error.error.message);
      }
    });
  }
}
