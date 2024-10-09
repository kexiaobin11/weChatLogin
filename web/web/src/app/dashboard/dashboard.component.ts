import {Component, NgZone, OnInit, SecurityContext} from '@angular/core';
import {UserService} from "../../service/user.service";
import {filter, first} from "rxjs/operators";
import {User} from "../../entity/user";
import {Router} from "@angular/router";
import {DomSanitizer, SafeResourceUrl, SafeUrl} from "@angular/platform-browser";
import {WechatQrCode} from "../../entity/wechat-qr-code";
import {Subscription} from 'rxjs';

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
  private subscription: Subscription | null = null;

  constructor(private userService: UserService,
              private router: Router,
              private ngZone: NgZone,
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
      this.userService.checkScanBind().subscribe(() => {
        this.ngZone.run(() => {
          this.userService.initCurrentLoginUser().subscribe(value => {
            if (value.wechatUser !== null) {
              clearInterval(this.intervalId); // 条件满足时停止轮询
              this.user!.wechatUser = value.wechatUser;
              this.isShowQrCode = false;
            }
          });
        });
      });
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
    this.userService.eventSource?.close();
  }
}
