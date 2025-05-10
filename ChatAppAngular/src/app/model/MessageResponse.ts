export interface MessageResponse {
  id: number;
  content: string;
  senderId: number;
  receiverId: number;
  createdDate: Date;
}
