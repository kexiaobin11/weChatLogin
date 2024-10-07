package com.yunzhi.ssewechat.config;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.filter.XAuthTokenAfterFilter;
import com.yunzhi.ssewechat.filter.XAuthTokenBeforeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class MvcSecurityConfig {
    public static int order = 0;

    private final PasswordEncoder passwordEncoder;
    private final XAuthTokenBeforeFilter xAuthTokenBeforeFilter = new XAuthTokenBeforeFilter();
    private final XAuthTokenAfterFilter xAuthTokenAfterFilter = new XAuthTokenAfterFilter();

    public MvcSecurityConfig() {
        this.passwordEncoder = new YzBCryptPasswordEncoder();
        User.setPasswordEncoder(this.passwordEncoder);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .csrf(csrf -> csrf.disable())  // 禁止csrf，否则在进行post等请求时，需要在传输数据中加签，后台会进行验签
                    .addFilterBefore(xAuthTokenBeforeFilter, BasicAuthenticationFilter.class)
                    .addFilterAfter(xAuthTokenAfterFilter, BasicAuthenticationFilter.class)
                    .authorizeHttpRequests(authorization ->
                            authorization.requestMatchers("wx/**").permitAll()
                                    .requestMatchers("user/checkScan").permitAll()
                                    .anyRequest().authenticated()
                    )
                    .httpBasic(withDefaults()); // 启用HTTP基本认证
            return http.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }
}
