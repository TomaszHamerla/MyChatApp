export interface AuthReq {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  id: number;
}
