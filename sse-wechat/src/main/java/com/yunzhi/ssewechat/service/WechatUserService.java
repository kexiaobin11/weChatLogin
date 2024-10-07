package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;

public interface WechatUserService {
    /**
     * @param openId 微信公众号的用户唯一标识
     * @param appId 微信公众号的唯一标识
     * @return 如果有数据库有直接返回，没有进行创建，为了接下来user跟Wechat进行绑定
     */
    WechatUser getOneByOpenidAndAppId(String openId, String appId);

    /**
     * 微信绑定用户
     * @param weChatUser 微信用户
     * @param user 当前登录用户
     */
    void bindWeChatUserToUserFromUser(WechatUser weChatUser, User user);

    /**
     * 微信绑定用户
     * @param sceneStr 生成二维码的场景值
     * @param openId 微信公众号的用户唯一标识
     * @param appId 微信公众号的唯一标识
     */
    void bindWxSceneStrToWeChatUser(String sceneStr, String openId, String appId);
}
