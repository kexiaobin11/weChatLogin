package com.yunzhi.ssewechat.exception;

/**
 * 未能成功获取二维码异常
 */
public class QrCodeCanNotGetException extends Exception {
    public QrCodeCanNotGetException(String message) {
        super(message);
    }
}
