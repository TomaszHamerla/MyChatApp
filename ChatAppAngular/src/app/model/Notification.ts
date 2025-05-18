import {MessageType} from "./MessageType";

export interface Notification {
  chatId?: number;
  content?: string;
  senderId?: number;
  receiverId?: number;
  chatName?: string;
  createdDate?: Date;
  type: MessageType;
}
