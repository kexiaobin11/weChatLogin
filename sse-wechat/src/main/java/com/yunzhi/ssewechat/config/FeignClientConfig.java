package com.yunzhi.ssewechat.config;

import feign.Feign;
import feign.Request;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Configuration
@Component
public class FeignClientConfig {
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignClientConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    /**
     * 根据自定义的Feign请求接口，自定义请求的 URL 来创建请求客户端
     *
     * @param type 根据该类型，去解析这个类型的注解，根据注解来生成请求对象
     * @param url  自定义的请求 URL
     * @return
     */
    public <T> T  createClient(Class<T> type, String url) {
        Encoder encoder = new SpringEncoder(this.messageConverters);
        Decoder decoder = new SpringDecoder(this.messageConverters);

        return Feign.builder()
                .encoder(encoder)
                .decoder(decoder)
                .options(new Request.Options(5, TimeUnit.SECONDS, 3, TimeUnit.MINUTES, false))
                .target(type, url);
    }
}
