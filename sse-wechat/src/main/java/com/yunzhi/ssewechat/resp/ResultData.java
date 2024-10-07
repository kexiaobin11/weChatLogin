package com.yunzhi.ssewechat.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ResultData<T> {
    private Integer code;

    private String message;

    private T data;

    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        return custom(ReturnCodeEnum.RC200.getCode(), ReturnCodeEnum.RC200.getMessage(), data);
    }

    public static <T> ResultData<T> success(Integer code, String massage, T data) {
        return custom(code, massage, data);
    }

    public static <T> ResultData<T> fail(Integer code, String massage) {
        return custom(code, massage, null);
    }

    public static <T> ResultData<T> fail() {
        return custom(ReturnCodeEnum.RC999.getCode(), ReturnCodeEnum.RC999.getMessage(), null);
    }

    public static <T> ResultData<T> fail(String massage) {
        return custom(9999, massage, null);
    }

    public static <T> ResultData<T> custom(Integer code, String massage, T data) {
        ResultData<T> result = new ResultData<>();
        result.setCode(code);
        result.setMessage(massage);
        result.setData(data);
        return result;
    }
}
