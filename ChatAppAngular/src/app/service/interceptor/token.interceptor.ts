import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from "@angular/core";
import {TokenService} from "../utils/token.service";

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(TokenService).token;
  if (token) {
    const cloneReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
    return next(cloneReq);
  }
  return next(req);
};
