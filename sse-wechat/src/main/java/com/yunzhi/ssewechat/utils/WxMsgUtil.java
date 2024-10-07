package com.yunzhi.ssewechat.utils;

import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import org.apache.commons.lang3.StringUtils;
public class WxMsgUtil {
    // 事件-关注
    private static String EVENT_SUBSCRIBE = "subscribe";
    // 事件-扫码
    private static String Event_SCAN = "SCAN";

    public static boolean isScanQrCode(String ticket) {
        return StringUtils.isNotBlank(ticket);
    }

    public static boolean isEventAndSubscribe(String event) {
        return StringUtils.equals(event, EVENT_SUBSCRIBE);
    }

    public static boolean isEventAndScan(String event) {
        return StringUtils.equals(event, Event_SCAN);
    }

    public static String getReplyTextMsg(WxMpXmlMessage wxMessage, String message) {
        WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().content(message).fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
        return outTextMessage.toXml();
    }
}
