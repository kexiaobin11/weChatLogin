import {Injectable, NgZone} from '@angular/core';
import {Observable, of, ReplaySubject, Subject} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {User} from '../entity/user';
import {catchError, map, tap} from 'rxjs/operators';

declare var require: any;
const {v4: uuid} = require('uuid');

import {WechatQrCode} from '../entity/wechat-qr-code';
import {ResultData} from '../entity/result-data';
import {Router} from '@angular/router';
import {XAuthTokenInterceptor} from '../interceptor/x-auth-token.interceptor';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  protected baseUrl = 'user';
  private currentLoginUser: User | null | undefined;
  public eventSource: EventSource | null = null;
  /**
   * buffer 设置为 1
   * 只保留最新的登录用户
   */
  private currentLoginUser$ = new ReplaySubject<User>(1);

  /**
   * 登录二维码
   */
  constructor(protected httpClient: HttpClient,
              protected router: Router,
              private ngZone: NgZone) {
  }

  checkScan(sceneStr: string): Observable<ResultData> {
    return new Observable<ResultData>((observer) => {
      const eventSource = new EventSource('api/user/checkScan/' + sceneStr);  // 连接后端 SSE 端点
      eventSource.onmessage = (event) => {
        this.ngZone.run(() => {
          const result = JSON.parse(event.data) as ResultData;
          if (result.code !== 1070) {
            XAuthTokenInterceptor.setToken(event.lastEventId);
            observer.next(result);
            observer.complete();
            eventSource.close();
          }
        });
      };
      eventSource.onerror = (error) => {
        observer.error(error);
        eventSource.close();
      };
    });
  }

  checkScanBind(): Observable<ResultData> {
    return new Observable<ResultData>((observer) => {
      const token = XAuthTokenInterceptor.getToken();
      this.eventSource = new EventSource('api/user/checkScanBind/' + token);  // 连接后端 SSE 端点
      this.eventSource.onmessage = (event) => {
        this.ngZone.run(() => {
          const result = JSON.parse(event.data) as ResultData;
          if (result.code !== 1070) {
            observer.next(result);
            observer.complete();
            this.eventSource?.close();
          }
        });
      };
      this.eventSource.onerror = (error) => {
        observer.error(error);
        this.eventSource?.close();
      };
    });
  }

  /**
   * 生成绑定的二维码
   */
  generateBindQrCode(): Observable<WechatQrCode> {
    const body = {sceneStr: XAuthTokenInterceptor.getToken()};
    return this.httpClient.post<WechatQrCode>(`${this.baseUrl}/generatorBindQrCode`, body);
  }


  /**
   * 获取当前登录用户
   */
  getCurrentLoginUser$(): Observable<User> {
    return this.currentLoginUser$;
  }

  /**
   * 获取登录二维码
   */
  getLoginQrCode(): Observable<WechatQrCode> {
    const body = {sceneStr: uuid()};
    return this.httpClient.post<WechatQrCode>('wx/generatorQrCode', body);
  }

  /**
   * 请求当前登录用户
   */
  initCurrentLoginUser(callback?: () => void): Observable<User> {
    return new Observable<User>(subscriber => {
      this.httpClient.get<User>(`${this.baseUrl}/currentLoginUser`)
        .subscribe((user: User) => {
            this.setCurrentLoginUser(user);
            subscriber.next(user);
          }, error => {
            if (callback) {
              callback();
            }
            this.setCurrentLoginUser(undefined);
            subscriber.error(error);
          },
          () => {
            if (callback) {
              callback();
            }
            subscriber.complete();
          });
    });
  }


  logout(): Observable<void> {
    return this.httpClient.get<void>(`${this.baseUrl}/logout`).pipe(map(() => {
      this.setCurrentLoginUser(undefined);
    }));
  }

  login(user: { username: string, password: string }): Observable<User> {
    // 新建Headers，并添加认证信息
    let headers = new HttpHeaders();
    // 添加认证信息
    headers = headers.append('Authorization',
      'Basic ' + btoa(user.username + ':' + encodeURIComponent(user.password)));

    // 发起get请求并返回
    return this.httpClient.get<User>(`user/login`, {headers})
      .pipe(tap(data => this.setCurrentLoginUser(data)));
  }


  /**
   * 设置当前登录用户
   * @param user 登录用户
   */
  setCurrentLoginUser(user: User | undefined): void {
    this.currentLoginUser = user;
    this.currentLoginUser$.next(user);
  }
}
