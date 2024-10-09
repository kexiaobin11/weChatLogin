package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface UserService {

    User findByUsername(String username);


    /**
     * 根据微信用户获取到用户
     *
     * @param wechatUser 微信用户
     * @return 返回用户，如果当前没有找到user,那就证明当前没有绑定当前用户，报未关联异常
     */
    User getByWechatUser(WechatUser wechatUser);

    Optional<User> getCurrentLoginUser();

    User findById(Long id);

    SseEmitter checkScan(String sceneStr);

    /**
     * This method checks the status of a scan binding process
     * identified by the provided token.
     *
     * @param token A unique token representing the scan binding process.
     * @return An SseEmitter object that allows real-time server-sent
     * events to be sent to the client during the scan binding process.
     */
    SseEmitter checkScanBind(String token);
}
