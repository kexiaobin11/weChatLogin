package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import com.yunzhi.ssewechat.filter.XAuthTokenBeforeFilter;
import com.yunzhi.ssewechat.model.ExpiredHashMap;
import com.yunzhi.ssewechat.model.WechatQrCode;
import com.yunzhi.ssewechat.utils.WxMsgUtil;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WxServiceImpl implements WxService {
    private static final Logger logger = LoggerFactory.getLogger(WxServiceImpl.class);
    private final WxMpService wxMpService;
    private final WechatUserService wechatUserService;

    @Value("${wx.mp.app-id}")
    private String appId;

    public WxServiceImpl(WxMpService wxMpService, WechatUserService wechatUserService) {
        this.wxMpService = wxMpService;
        this.wechatUserService = wechatUserService;
    }

    @Override
    public void checkSignature(String timestamp, String nonce, String signature) {
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            logger.error("签名校验失败，非法请求");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "签名校验失败，非法请求");
        }
        logger.info("签名校验成功");
    }

    @Override
    public WechatQrCode generatorQrCode(String sceneStr) {
        try {
            WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(sceneStr, 300);
            String qrCodeUrl = wxMpService.getQrcodeService().qrCodePictureUrl(ticket.getTicket());

            WechatQrCode wechatQrCode = new WechatQrCode();
            wechatQrCode.setQrCodeUrl(qrCodeUrl);
            wechatQrCode.setSceneStr(sceneStr);
            return wechatQrCode;
        } catch (WxErrorException e) {
            logger.error("Error generating QR code", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate QR code", e);
        }
    }

    @Override
    public String handlerMxMessage(String requestBody, String signature, String timestamp, String nonce) {
        this.checkSignature(timestamp, nonce, signature);
        try {
            WxMpXmlMessage wxMessage = WxMpXmlMessage.fromXml(requestBody);
            // 扫码登录事件，如果为SCAN事件为扫码登录
            if (WxMsgUtil.isEventAndScan(wxMessage.getEvent())) {
                return handleScanLogin(wxMessage);
            }
            // 如果为Subscribe绑定公众号事件
            if (WxMsgUtil.isEventAndSubscribe(wxMessage.getEvent())) {
                // 对场景值进行
                String token = wxMessage.getEventKey().replace("qrscene_", "");
                User user = XAuthTokenBeforeFilter.map.get(token);
                if (user != null) {
                    WechatUser wechatUser = this.wechatUserService.getOneByOpenidAndAppId(wxMessage.getFromUser(), this.appId);
                    this.wechatUserService.bindWeChatUserToUserFromUser(wechatUser, user);
                    WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().content("欢迎关注梦云智").fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser()).build();
                    return outTextMessage.toXml();
                }
            }
        } catch (Exception e) {
            logger.error("解析请求体出错: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "解析请求体出错", e);
        }
        return "success"; // 返回成功消息
    }

    @Override
    public String handleScanLogin(WxMpXmlMessage wxMessage) {
        this.wechatUserService.bindWxSceneStrToWeChatUser(wxMessage.getEventKey(), wxMessage.getFromUser(), this.appId);
        WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().content("登录成功").fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
        return outTextMessage.toXml();
    }
}
