import {Injectable} from '@angular/core';
import {MessageService} from "primeng/api";

@Injectable({
  providedIn: 'root',
})
export class ToastService {

  constructor(private messageService: MessageService) {
  }

  showInfo(message: string) {
    this.messageService.add({severity: 'info', summary: 'Info', detail: message});
  }

  showError(message: string) {
    this.messageService.add({severity: 'error', summary: 'Error', detail: message});
  }
}
