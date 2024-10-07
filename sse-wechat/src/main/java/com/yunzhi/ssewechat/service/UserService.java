package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import com.yunzhi.ssewechat.resp.ResultData;
import com.yunzhi.ssewechat.resp.ReturnCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface UserService {

    User findByUsername(String username);

    ResultData<User> checkScan(HttpServletRequest request, HttpServletResponse response, String sceneStr);

    /**
     * 根据微信用户获取到用户
     * @param wechatUser 微信用户
     * @return 返回用户，如果当前没有找到user,那就证明当前没有绑定当前用户，报未关联异常
     */
    User getByWechatUser(WechatUser wechatUser);

    Optional<User> getCurrentLoginUser();

    User findById(Long id);
}
