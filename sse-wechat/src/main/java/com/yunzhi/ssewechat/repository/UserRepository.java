package com.yunzhi.ssewechat.repository;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.entity.WechatUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UserRepository extends CrudRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByPhoneAndDeletedFalse(String phone);

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    Optional<User> findByWechatUser(WechatUser wechatUser);
}
