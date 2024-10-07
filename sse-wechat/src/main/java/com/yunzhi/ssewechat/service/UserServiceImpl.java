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

import java.util.Optional;
import java.util.UUID;

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
    public ResultData<User> checkScan(HttpServletRequest request, HttpServletResponse response, String sceneStr) {
        WechatUser wechatUser = WechatUserServiceImpl.map.get(sceneStr);
        if (wechatUser == null) {
            return ResultData.success(1070, "用户未扫码" , null);
        }
        User user = this.getByWechatUser(wechatUser);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String xAuthToken = request.getHeader("x-auth-token");
        if (xAuthToken == null || xAuthToken.isEmpty()) {
            xAuthToken = UUID.randomUUID().toString();
        }
        XAuthTokenBeforeFilter.map.put(xAuthToken, user);
        response.addHeader("x-auth-token", xAuthToken);
        WechatUserServiceImpl.map.remove(sceneStr);
        return ResultData.success(user);
    }

    @Override
    public User getByWechatUser(WechatUser wechatUser) {
        return userRepository.findByWechatUser(wechatUser).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Optional<User> getCurrentLoginUser() {
        logger.debug("初始化用户");
        Optional<User> user = null;

        logger.debug("获取用户认证信息");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.debug("根据认证信息查询用户");
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
    }
}
