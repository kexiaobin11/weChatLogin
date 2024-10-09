package com.yunzhi.ssewechat.service;

import com.yunzhi.ssewechat.filter.XAuthTokenBeforeFilter;
import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import com.yunzhi.ssewechat.repository.UserRepository;
import com.yunzhi.ssewechat.resp.ResultData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User getByWechatUser(WechatUser wechatUser) {
        return userRepository.findByWechatUser(wechatUser).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Optional<User> getCurrentLoginUser() {
        Optional<User> user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            user = userRepository.findByUsername(authentication.getName());
        }

        return user;
    }

    @Override
    public User findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public SseEmitter checkScan1(HttpServletRequest request, HttpServletResponse response, String sceneStr) {
        // 创建一个 SSE 发射器，设置超时时间为 30 秒
        SseEmitter emitter = new SseEmitter(30000L);
        AtomicBoolean isCompleted = new AtomicBoolean(false);

        new Thread(() -> {
            try {
                // 循环检测用户是否扫码
                while (!isCompleted.get()) {
                    // 根据 sceneStr 从缓存中获取 WeChat 用户信息
                    WechatUser wechatUser = WechatUserServiceImpl.map.get(sceneStr);

                    // 如果用户扫码成功
                    if (wechatUser != null) {
                        // 获取用户信息并进行认证
                        User user = getByWechatUser(wechatUser);

                        // 设置用户认证信息
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(user, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 生成 x-auth-token 并返回给客户端
                        String xAuthToken = request.getHeader("x-auth-token");
                        if (xAuthToken == null || xAuthToken.isEmpty()) {
                            xAuthToken = UUID.randomUUID().toString();
                        }
                        XAuthTokenBeforeFilter.map.put(xAuthToken, user);

                        // 将结果推送给前端，并附带 x-auth-token
                        if (!isCompleted.get()) {  // 检查是否已经完成
                            emitter.send(SseEmitter.event()
                                    .data(ResultData.success(null))
                                    .id(xAuthToken));
                        }

                        // 删除 sceneStr 对应的用户信息
                        WechatUserServiceImpl.map.remove(sceneStr);

                        // 完成 SSE 推送并关闭连接
                        isCompleted.set(true);
                        emitter.complete();
                        break;
                    }

                    // 如果用户尚未扫码，推送等待消息
                    if (!isCompleted.get()) {  // 检查是否已经完成
                        emitter.send(SseEmitter.event().data(ResultData.success(1070, "用户未扫码", null)));
                    }

                    // 等待 2 秒后再次检查
                    Thread.sleep(2000);
                }
            } catch (IOException | InterruptedException e) {
                // 如果出现异常，终止 SSE 连接
                isCompleted.set(true);
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }
}
