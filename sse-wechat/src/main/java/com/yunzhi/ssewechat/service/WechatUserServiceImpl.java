package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import com.yunzhi.ssewechat.model.ExpiredHashMap;
import com.yunzhi.ssewechat.repository.WeChatUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WechatUserServiceImpl implements WechatUserService {
    private final WeChatUserRepository weChatUserRepository;
    private final UserService userService;
    public final static ExpiredHashMap<String, WechatUser> map = new ExpiredHashMap<>();

    public WechatUserServiceImpl(WeChatUserRepository weChatUserRepository, UserService userService) {
        this.weChatUserRepository = weChatUserRepository;
        this.userService = userService;
    }

    @Override
    public WechatUser getOneByOpenidAndAppId(String openId, String appId) {
        return this.weChatUserRepository.findByOpenidAndAppId(openId, appId)
                .orElseGet(() -> this.weChatUserRepository.save(new WechatUser(openId, appId)));
    }

    @Override
    public void bindWeChatUserToUserFromUser(WechatUser weChatUser, User userDetails) {
        WechatUser wechatUser = this.weChatUserRepository.findById(weChatUser.getId()).get();
        User user = this.userService.findByUsername(userDetails.getUsername());
        wechatUser.setUser(user);
        this.weChatUserRepository.save(wechatUser);
    }

    @Override
    public void bindWxSceneStrToWeChatUser(String sceneStr, String openId, String appId) {
        WechatUser wechatUser = this.getOneByOpenidAndAppId(openId, appId);
        WechatUserServiceImpl.map.put(sceneStr, wechatUser);
    }
}
