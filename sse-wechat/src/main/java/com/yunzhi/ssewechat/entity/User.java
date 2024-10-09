package com.yunzhi.ssewechat.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

@Entity
public class User extends BaseEntity<Long> implements UserDetails {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String username;

    @Getter
    @JsonView(PasswordJsonView.class)
    private String password;

    @Getter
    private static PasswordEncoder passwordEncoder;

    @OneToOne(mappedBy = "user")
    @Getter
    @Setter
    @JsonView(WeChatUserJsonView.class)
    private WechatUser wechatUser;

    @Getter
    @Setter
    private String phone;

    public static void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        User.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public void setPassword(String password) {
        if (User.passwordEncoder == null) {
            throw new RuntimeException("未设置User实体的passwordEncoder，请调用set方法设置");
        }
        this.password = User.passwordEncoder.encode(password);
    }

    private interface PasswordJsonView {}

    public static interface WeChatUserJsonView {}
}


