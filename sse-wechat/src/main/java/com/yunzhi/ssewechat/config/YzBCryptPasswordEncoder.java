package com.yunzhi.ssewechat.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 自定义密码校验器.
 * 注意：其不能够声明为@Component组件出现，否则将触发DaoAuthenticationProvider的构造函数
 * 从而直接注册DelegatingPasswordEncoder校验器
 */
public class YzBCryptPasswordEncoder extends BCryptPasswordEncoder {

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        return super.matches(rawPassword, encodedPassword);
    }
}
