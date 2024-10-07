package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.model.WechatQrCode;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;


public interface WxService {
    WechatQrCode generatorQrCode(String sceneStr);

    String handlerMxMessage(String requestBody, String signature, String timestamp, String nonce);

    void checkSignature(String timestamp, String nonce, String signature);

    String handleScanLogin(WxMpXmlMessage wxMpXmlMessage);
}
