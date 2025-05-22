import {Component, OnDestroy, OnInit} from '@angular/core';
import {DynamicDialogConfig, DynamicDialogRef} from "primeng/dynamicdialog";
import {ChatService} from "../../service/api/chat.service";
import {Subscription} from "rxjs";
import {ToastService} from "../../service/utils/toast.service";
import {TrustUrlPipe} from "../../service/utils/trust-url.pipe";
import {CommonModule} from "@angular/common";
import {ProgressSpinner} from "primeng/progressspinner";

@Component({
  selector: 'app-image-preview',
  imports: [TrustUrlPipe, CommonModule, ProgressSpinner],
  templateUrl: './image-preview.component.html',
  styleUrl: './image-preview.component.css'
})
export class ImagePreviewComponent implements OnInit, OnDestroy {
  messageId: number;
  imageUrl: string = '';
  messageIds: number[] = [];
  currentIndex: number = 0;
  scale = 1;
  currentRotation = 0;
  fileType: string = '';
  subscription!: Subscription;
  translateX = 0;
  translateY = 0;

  private isDragging = false;
  private startX = 0;
  private startY = 0;

  constructor(
    private config: DynamicDialogConfig,
    private ref: DynamicDialogRef,
    private chatService: ChatService,
    private toastService: ToastService
  ) {
    this.messageId = this.config.data.messageId;
    this.messageIds = this.config.data.messageIds || [];
  }

  ngOnInit(): void {

      this.currentIndex = this.messageIds.indexOf(this.messageId);
      this.loadImage(this.messageId);
  }

  ngOnDestroy(): void {
    this.ref.close();
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private loadImage(messageId: number): void {
    this.imageUrl = '';
    this.subscription = this.chatService.getImage(messageId).subscribe({
      next: (response) => {
        if (response.body === null) {
          this.toastService.showError('Nie można pobrać pliku');
          this.ref.close();
          return;
        }
        this.fileType = response.body.type;
        const blob = new Blob([response.body], {type: this.fileType});
        this.imageUrl = URL.createObjectURL(blob);
      },
      error: () => {
        this.toastService.showInfo('Błąd podczas ładowania pliku');
        this.ref.close();
      }
    });
  }

  zoomIn(): void {
    this.scale += 0.1;
  }

  zoomOut(): void {
    this.scale = Math.max(0.1, this.scale - 0.1);
  }

  rotateImage(): void {
    this.scale = this.scale === -1 ? 1 : -1;
    this.currentRotation = (this.currentRotation + 90) % 360;
  }

  onWheel(event: WheelEvent): void {
    event.preventDefault();
    event.deltaY > 0 ? this.zoomOut() : this.zoomIn();
  }

  startDrag(event: MouseEvent): void {
    this.isDragging = true;
    this.startX = event.clientX - this.translateX;
    this.startY = event.clientY - this.translateY;
    event.preventDefault();
  }

  onMouseMove(event: MouseEvent): void {
    if (this.isDragging) {
      this.translateX = event.clientX - this.startX;
      this.translateY = event.clientY - this.startY;
    }
  }

  stopDrag(): void {
    this.isDragging = false;
  }

  nextImage(): void {
    if (this.currentIndex < this.messageIds.length - 1) {
      this.currentIndex++;
      this.loadImage(this.messageIds[this.currentIndex]);
    }
  }

  previousImage(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
      this.loadImage(this.messageIds[this.currentIndex]);
    }
  }
}
