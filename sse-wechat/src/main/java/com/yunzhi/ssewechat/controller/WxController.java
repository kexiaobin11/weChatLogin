package com.yunzhi.ssewechat.controller;

import com.yunzhi.ssewechat.dto.UserDto;
import com.yunzhi.ssewechat.dto.WechatQrCodeDto;
import com.yunzhi.ssewechat.model.WechatQrCode;
import com.yunzhi.ssewechat.service.WxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController()
@RequestMapping("wx")
public class WxController {
    private final WxService wxService;

    public WxController(WxService wxService) {
        this.wxService = wxService;
    }

    @GetMapping("send")
    public String configAccess(String signature, String timestamp, String nonce, String echostr) {
        this.wxService.checkSignature(timestamp, nonce, signature);
        return echostr;
    }

    @PostMapping("send")
    public String handlerMxMessage(@RequestBody String requestBody, @RequestParam("signature") String signature,
                                   @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) {
       return this.wxService.handlerMxMessage(requestBody, signature, timestamp, nonce);
    }

    /**
     * 生成微信二维码
     * @return 二维码地址和场景值，场景值的最用是为了进行请求后台当前有没有登录
     */
    @PostMapping("generatorQrCode")
    public WechatQrCode generatorQrCode(@RequestBody WechatQrCodeDto.GeneratorQrCode generatorQrCode){
        return this.wxService.generatorQrCode(generatorQrCode.getSceneStr());
    }
}
