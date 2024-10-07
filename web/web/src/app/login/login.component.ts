import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from "../../service/user.service";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {User} from "../../entity/user";
import {WechatQrCode} from "../../entity/wechat-qr-code";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginModel = 'username' as 'username' | 'wechat';

  weChatQrCode: WechatQrCode = {} as WechatQrCode;

  /**
   * 登录字段
   */
  formKeys = {
    username: 'username',
    password: 'password',
  };

  /** 登录表单对象 */
  loginForm: FormGroup;

  /** 错误信息 */
  errorInfo: string | undefined;

  /** 提交状态 */
  submitting = false;


  constructor(private builder: FormBuilder,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private userService: UserService,
              private sanitizer: DomSanitizer) {
    /** 创建表单 */
    this.loginForm = this.builder.group({
      username: ['', [Validators.minLength(5),
        Validators.maxLength(11),
        Validators.pattern('\\w+'),
        Validators.required]],
      password: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.errorInfo = '';
    this.loginForm.valueChanges
      .subscribe(() => {
        this.errorInfo = '';
      });
  }

  onLogin(): void {
    const user = {
      username: this.loginForm.get('username')!.value as string,
      password: this.loginForm.get('password')!.value as string,
    };
    this.login(user);
  }

  login(user: { username: string, password: string }) {
    this.userService.login(user)
      .subscribe(() => {
        this.userService.initCurrentLoginUser().subscribe({
          next: () => this.router.navigateByUrl('dashboard').then()
        });
      }, (response) => {
        this.errorInfo = '登录失败，请检查您填写的信息是否正确, 如若检查无误，可能是您的账号被冻结';
      });
  }

  /**
   * 微信扫码登录
   */
  onWeChatLogin() {
    this.userService.getLoginQrCode()
      .subscribe(value => {
        this.weChatQrCode = value;
        this.loginModel = 'wechat';
        this.startPolling(value.sceneStr);
      });
  }

  startPolling(sceneStr: string) {
    const pollingInterval = 2000; // 每2秒轮询一次
    let intervalId = setInterval(() => {
      // 仅对 checkScan 进行轮询
      this.userService.checkScan(sceneStr).subscribe(v => {
        if (v.data !== null && v.code !== 1070) {
          this.userService.setCurrentLoginUser(v.data as User);
          clearInterval(intervalId); // 满足条件时停止轮询
          this.router.navigateByUrl('dashboard').then();
        }
      });
    }, pollingInterval);
  }
}
