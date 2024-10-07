package com.yunzhi.ssewechat.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ExpiredData<T> {
    private final static int defaultExpiredMinute = 120;
    T data;
    long expiredTimestamp;
    int expiredMinutes;

    boolean getIsExpired() {
        return System.currentTimeMillis() > this.expiredTimestamp;
    }

    ExpiredData(@NotNull T data, int expiredMinutes) {
        this.expiredMinutes = expiredMinutes;
        this.data = data;
        this.renew();
    }

    ExpiredData(@NotNull T data) {
        this(data, defaultExpiredMinute);
    }

    void renew() {
        this.expiredTimestamp = (long) this.expiredMinutes * 60 * 1000 + System.currentTimeMillis();
    }
}
