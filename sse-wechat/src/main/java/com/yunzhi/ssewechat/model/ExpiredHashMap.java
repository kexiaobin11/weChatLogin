package com.yunzhi.ssewechat.model;

import java.util.concurrent.ConcurrentHashMap;

public class ExpiredHashMap<K, V> {
    private final ConcurrentHashMap<K, ExpiredData<V>> map = new ConcurrentHashMap<>();

    public V get(K key) {
        if (System.currentTimeMillis() % 10 == 0) {
            this.clear();
        }
        ExpiredData<V> value = this.map.get(key);
        if (value == null) {
            return null;
        }

        if (value.getIsExpired()) {
            this.remove(key);
            return null;
        }

        value.renew();
        return value.getData();
    }

    private void clear() {
        map.forEach((key, value) -> {
            if (value.getIsExpired()) {
                map.remove(key);
            }
        });
    }

    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    public V put(K key, V value) {
        ExpiredData<V> result = this.map.put(key, new ExpiredData<V>(value));
        return null == result ? null : result.getData();
    }

    public void remove(K key) {
        this.map.remove(key);
    }
}
