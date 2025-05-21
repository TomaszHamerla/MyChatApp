import {Component, effect, ElementRef, Input, Signal, ViewChild, WritableSignal} from '@angular/core';
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
import {ProgressSpinner} from "primeng/progressspinner";
import {PickerComponent} from "@ctrl/ngx-emoji-mart";
import {EmojiData} from "@ctrl/ngx-emoji-mart/ngx-emoji";
import {Dialog} from "primeng/dialog";
import {MessageType} from "../../model/MessageType";

@Component({
  selector: 'app-chat-window',
  imports: [CommonModule, InputTextModule, FormsModule, ButtonModule, ProgressSpinner, PickerComponent, Dialog],
  templateUrl: './chat-window.component.html',
  styleUrl: './chat-window.component.css'
})
export class ChatWindowComponent {
  @Input({required: true})
  selectedChat!: Signal<ChatResponse | null>;
  @Input({required: true})
  notifications!: WritableSignal<Notification | null>;
  text = '';
  messages: MessageResponse[] = [];
  @ViewChild('scrollableDiv')
  scrollableDiv!: ElementRef<HTMLDivElement>;
  senderId: number;
  currentPage = 0;
  pageSize = 20;
  allMessagesLoaded = false;
  loading = false;
  showEmojis = false;
  visibleEditUserDialog = false;
  chatName: string = '';
  readonly MessageType = MessageType;

  constructor(
    private chatService: ChatService,
    private toastService: ToastService
  ) {
    const storedId = localStorage.getItem('senderId');
    this.senderId = storedId ? +storedId : 0;
    effect(() => {
      const chat = this.selectedChat();
      if (chat) {
        this.prepareChatName();
        this.currentPage = 0;
        this.pageSize = 20;
        this.allMessagesLoaded = false;
        this.messages = [];
        this.getMessages(chat.id);
      } else {
        this.notifications.set(null);
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
            receiverId: notification.receiverId ?? 0,
            type: notification.type ?? MessageType.TEXT,
            createdDate: notification.createdDate ?? new Date(),
          }
          console.log(newMessage)
          this.updateUnreadMsgLineInfo(chat);
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
            receiverId: messageRequest.receiverId,
            createdDate: new Date(),
            type: MessageType.TEXT,
          });
          this.text = '';
        },
        error: (error) => {
          this.toastService.showError(error.error.message);
        },
        complete: () => {
          this.scrollToBottom();
          this.messages = this.messages.filter(msg => !msg.isMsgCountInfo);
          const chat = this.selectedChat();
          if (chat) {
            chat.unreadMessages = 0;
          }
        }
      });
    }
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMsg();
    }
  }

  loadMoreMessages(): void {
    const chat = this.selectedChat();
    if (chat) {
      this.getMessages(chat.id, true);
    }
  }

  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.text += emoji.native;
    this.showEmojis = false;
  }

  setMessageToSeen() {
    const chat = this.selectedChat();
    if (chat) {
      this.chatService.setMessageToSeen(chat.id).subscribe({
        next: () => {
          chat.unreadMessages = 0;
        },
        error: (error) => {
          this.toastService.showError(error.error.message);
        }
      });
    }
  }

  openUserEditDialog() {
    this.visibleEditUserDialog = true;
  }

  updateUserNick(value: string) {
    const chat = this.selectedChat();
    if (chat) {
      const userId = this.getReceiverId(chat);
      const chatId = chat.id
      this.chatService.updateNickName(userId, chatId, value).subscribe({
        next: () => {
          this.toastService.showInfo('Nick został zaktualizowany');
          this.visibleEditUserDialog = false;
          this.chatName = value;
        },
        error: (error) => {
          this.toastService.showError(error.error.message);
          this.visibleEditUserDialog = false;
        }
      });
    }
    this.visibleEditUserDialog = false;
  }

  onFileSelected(event: Event) {
    const chat = this.selectedChat();
    if (!chat) return;
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const messageRequest: MessageRequest = {
        content: file.name,
        senderId: this.getSenderId(chat),
        receiverId: this.getReceiverId(chat),
        chatId: chat.id
      };
      this.chatService.uploadFile(file, messageRequest).subscribe({
        next: (res) => {
          this.toastService.showInfo('Plik został wysłany');
          this.messages.push(res);
        },
        error: (err) => {
          const errorResponse = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastService.showError(errorResponse.errorDescription ?? errorResponse.error);
        },
        complete: () => {
          this.scrollToBottom();
        }
      });
    }
  }

  downloadFile(message: MessageResponse): void {
    const messageId = message.id;
    if (!messageId) return;

    this.chatService.downloadFile(messageId).subscribe(response => {
      const contentDisposition = response.headers.get('Content-Disposition');
      let fileName = '';
      if (!contentDisposition) {
        fileName = this.messages.find(m => m.id === messageId)?.content ?? 'plik';
      } else {
        const fileNameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
        fileName = fileNameMatch?.[1] ?? 'plik';
      }

      if (response.body !== null) {
        const blob = new Blob([response.body], {type: 'application/octet-stream'});
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      } else {
        this.toastService.showError('Nie można pobrać pliku');
      }
    });
  }


  private prepareChatName() {
    const chat = this.selectedChat();
    if (chat) {
      this.chatName = chat.senderNick ?? chat.name;
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

  private getMessages(id: number, loadMore = false) {
    if (this.allMessagesLoaded) return;
    this.loading = true;
    this.chatService.getMessages(id, this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        const newMessages = response.content;

        if (response.last) {
          this.allMessagesLoaded = true;
        }

        this.messages = [...newMessages.reverse(), ...this.messages];
        this.currentPage++;
      },
      error: (error) => {
        this.toastService.showError(error.error.message);
        this.loading = false;
      },
      complete: () => {
        this.scrollToBottom(loadMore);
        this.addUnreadMsg();
        this.loading = false;
      }
    });
  }

  private scrollToBottom(loadMore: boolean = false): void {
    if (this.scrollableDiv && !loadMore) {
      setTimeout(() => {
        const div = this.scrollableDiv.nativeElement;
        div.scrollTo({
          top: div.scrollHeight,
          behavior: 'smooth'
        });
      }, 200);
    }
  }

  private addUnreadMsg() {
    const chat = this.selectedChat();
    if (chat && chat.unreadMessages > 0) {
      const msgCountInfo: MessageResponse = {
        isMsgCountInfo: true
      };
      const index = this.messages.length - chat.unreadMessages;
      this.messages.splice(index, 0, msgCountInfo);
      this.setMessageToSeen();
    }
  }

  private updateUnreadMsgLineInfo(chat: ChatResponse) {
    const msgCountInfo: MessageResponse = {
      isMsgCountInfo: true
    };
    this.messages = this.messages.filter(msg => !msg.isMsgCountInfo);
    const index = this.messages.length - chat.unreadMessages;
    this.messages.splice(index, 0, msgCountInfo);
  }
}
