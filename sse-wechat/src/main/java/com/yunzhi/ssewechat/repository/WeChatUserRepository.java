package com.yunzhi.ssewechat.repository;

import com.yunzhi.ssewechat.entity.WechatUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * 微信(公众号)用户
 */
public interface WeChatUserRepository extends CrudRepository<WechatUser, Long> {

  Optional<WechatUser> findByOpenid(String openid);

  Optional<WechatUser> findByOpenidAndAppId(String openid, String appId);
}
