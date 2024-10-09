package com.yunzhi.ssewechat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WechatUser extends BaseEntity<Long> {

    private String openid;

    private String appId;

    @OneToOne
    @JsonView(UserJsonView.class)
    @JsonBackReference
    private User user;

    public WechatUser(String openid, String appId) {
        this.openid = openid;
        this.appId = appId;
    }

    public WechatUser(User user, String openid, String appId) {
        this.setUser(user);
        this.openid = openid;
        this.appId = appId;
    }

    public WechatUser() {
    }

    interface UserJsonView {}
}
