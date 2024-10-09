import {Component, OnInit, SecurityContext} from '@angular/core';
import {UserService} from "../../service/user.service";
import {filter, first} from "rxjs/operators";
import {User} from "../../entity/user";
import {Router} from "@angular/router";
import {DomSanitizer, SafeResourceUrl, SafeUrl} from "@angular/platform-browser";
import {WechatQrCode} from "../../entity/wechat-qr-code";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  user: User | undefined;
  isShowQrCode = false;
  private intervalId: any;
  wechatQrCode: WechatQrCode = {} as WechatQrCode;

  constructor(private userService: UserService,
              private router: Router,
              private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.userService.initCurrentLoginUser()
      .pipe(filter(v => v !== null && v !== undefined))
      .subscribe((data: User) => {
        this.setUser(data);
      });
  }

  setUser(user: User): void {
    this.user = user;
  }

  onBindWeChat(): void{
    this.userService.generateBindQrCode().subscribe(v => {
      this.wechatQrCode = v;
      this.isShowQrCode = true;
      this.startPolling();
    });
  }

  onLogout(): void {
    /**
     * complete 时跳转
     */
    this.userService.logout()
      .subscribe(() => {
        }, (error) => {
          console.error('error', error);
        },
        () => {
          this.router.navigateByUrl('login').then();
        }
      );
  }

  onClose(): void {
    this.isShowQrCode = false;
    if (this.intervalId) {
      clearInterval(this.intervalId); // 关闭时停止轮询
    }
  }

  startPolling(): void {
    const pollingInterval = 2000; // 每2秒轮询一次
    this.intervalId = setInterval(() => {
      // 仅对 checkScan 进行轮询
      this.userService.initCurrentLoginUser().subscribe(v => {
        if (v.wechatUser !== null) {
          clearInterval(this.intervalId); // 条件满足时停止轮询
          this.user!.wechatUser = v.wechatUser;
          this.isShowQrCode = false;
        }
      });
    }, pollingInterval);
  }
}
