package com.yunzhi.ssewechat.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WechatServiceClientTest {
    @Autowired
    private WxMpService wxMpService;

    @Test()
    public void test() {
        try {
            System.out.println(wxMpService.getAccessToken());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }
}