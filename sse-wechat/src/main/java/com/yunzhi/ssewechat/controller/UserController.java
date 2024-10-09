package com.yunzhi.ssewechat.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.yunzhi.ssewechat.dto.UserDto;
import com.yunzhi.ssewechat.dto.WechatQrCodeDto;
import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.model.WechatQrCode;
import com.yunzhi.ssewechat.resp.ResultData;
import com.yunzhi.ssewechat.service.UserService;
import com.yunzhi.ssewechat.service.WxService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@RestController
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final WxService wxService;

    public UserController(UserService userService, WxService wxService) {
        this.userService = userService;
        this.wxService = wxService;
    }

    @GetMapping(value = "/checkScan/{sceneStr}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @JsonView(CheckScanJsonView.class)
    public SseEmitter checkScan(@PathVariable String sceneStr) {
        return this.userService.checkScan(sceneStr);
    }

    @GetMapping(value = "/checkScanBind/{token}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter checkScanBind(@PathVariable String token) {
        return this.userService.checkScanBind(token);
    }

    @GetMapping("login")
    @JsonView(LoginJsonView.class)
    User login(Principal principal) {
        return this.userService.findByUsername(principal.getName());
    }

    @GetMapping("logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }

    @GetMapping("currentLoginUser")
    @JsonView(GetCurrentLoginUserJsonView.class)
    public User getCurrentLoginUser(Principal principal) {
        User user = principal == null ? null : this.userService.getCurrentLoginUser().get();
        if (user != null) {
            user = this.userService.findById(user.getId());
        }
        return user;
    }

    @PostMapping("generatorBindQrCode")
    public WechatQrCode generateBindQrCode(@RequestBody WechatQrCodeDto.GenerateBindQrCode generateBindQrCode) {
        return this.wxService.generatorQrCode(generateBindQrCode.getSceneStr());
    }

    interface WeChatUserJsonView extends User.WeChatUserJsonView {
    }

    interface CheckScanJsonView extends User.WeChatUserJsonView {
    }

    interface GetCurrentLoginUserJsonView extends User.WeChatUserJsonView {
    }

    interface LoginJsonView extends User.WeChatUserJsonView {
    }
}
