package com.yunzhi.ssewechat.dto;

import lombok.Data;

public class WechatQrCodeDto {
    @Data
    public static class GeneratorQrCode {
        String sceneStr;
    }

    @Data
    public static class GenerateBindQrCode {
        String sceneStr;
    }
}
