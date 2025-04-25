package org.xiaoxingbomei.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpMethodEmun {
    PUT("put"),
    GET("get"),
    POST("post"),
    DELETE("delete");
    private final String code;

    public static HttpMethodEmun getByCode(String code) {
        for (HttpMethodEmun value : HttpMethodEmun.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}