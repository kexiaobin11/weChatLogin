package com.yunzhi.ssewechat.startup;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import com.yunzhi.ssewechat.repository.UserRepository;
import com.yunzhi.ssewechat.repository.WeChatUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class CommandLineRunnerImpl implements CommandLineRunner, Ordered {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final WeChatUserRepository weChatUserRepository;

    public CommandLineRunnerImpl(UserRepository userRepository, WeChatUserRepository weChatUserRepository) {
        this.userRepository = userRepository;
        this.weChatUserRepository = weChatUserRepository;
    }

    @Override
    public void run(String... args) throws Exception {
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
