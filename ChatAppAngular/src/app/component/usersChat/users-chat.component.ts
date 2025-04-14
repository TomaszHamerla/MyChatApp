import {Component, OnInit} from '@angular/core';
import {UsersService} from "../../service/api/users.service";
import {UserResponse} from "../../model/UserResponse";
import {ToastService} from "../../service/utils/toast.service";
import {FormsModule} from "@angular/forms";
import {Listbox} from "primeng/listbox";

@Component({
  selector: 'app-users-chat',
  imports: [FormsModule, Listbox],
  templateUrl: './users-chat.component.html',
  styleUrl: './users-chat.component.css'
})
export class UsersChatComponent implements OnInit {
  users: UserResponse[] = [];
  selectedUser!: UserResponse;

  constructor(
    private usersService: UsersService,
    private toastService: ToastService,
  ) {
  }

  ngOnInit(): void {
    this.getUsers();
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
}
