package org.xiaoxingbomei.common.entity.response;

import lombok.Data;
import org.xiaoxingbomei.common.Enum.GlobalCodeEnum;

import java.io.Serializable;

/**
 * 通用响应体
 */
@Data
public class GlobalResponse<T> implements Serializable
{
    private static final long serialVersionUID  = 1L;

    private T       data;            // 响应主体
    private String  code;            // 响应码
    private String  message;         // 技术message
    private String  userMessage;     // 用户message
    private String  businessMessage; // 业务message

    public static final String SUCCESS = "0";

    public static final String ERROR = "-1";

    // 无参构造
    public GlobalResponse() {}

    // 有参构造
    public GlobalResponse(T data, String code, String message, String userMessage, String businessMessage)
    {
        this.data = data;
        this.code = code;
        this.message = message;
        this.userMessage = userMessage;
        this.businessMessage = businessMessage;
    }

    // success-data
    public static <T> GlobalResponse<T> success(T data)
    {
        GlobalResponse globalEntity = new GlobalResponse(data, GlobalCodeEnum.SUCCESS.getCode(), GlobalCodeEnum.SUCCESS.getMessage(), "", "");
        return globalEntity;
    }

    // success-data,businessMessage
    public static <T> GlobalResponse<T> success(T data, String userMessage)
    {
        GlobalResponse globalEntity = new GlobalResponse(data, GlobalCodeEnum.SUCCESS.getCode(), GlobalCodeEnum.SUCCESS.getMessage(), userMessage, "");
        return globalEntity;
    }

    // error-data
    public GlobalResponse<T> error(T data)
    {
        this.data = data;
        this.code = ERROR;
        return this;
    }

    // success-message
    public static <T> GlobalResponse<T> success(String message)
    {
        GlobalResponse globalEntity = new GlobalResponse(null, GlobalCodeEnum.SUCCESS.getCode(), message, message, message);
        return globalEntity;
    }

    // success-all
    public static <T> GlobalResponse<T> success(T data, String code, String message, String userMessage, String businessMessage)
    {
        GlobalResponse globalEntity = new GlobalResponse(data, code, message, userMessage, businessMessage);
        return globalEntity;
    }

    // error-message
    public static <T> GlobalResponse<T> error(String message)
    {
        GlobalResponse globalEntity = new GlobalResponse(null, GlobalCodeEnum.ERROR.getCode(), message, message, message);
        return globalEntity;
    }

    // error-data,businessMessage
    public static <T> GlobalResponse<T> error(T data, String userMessage)
    {
        GlobalResponse globalEntity = new GlobalResponse(data, GlobalCodeEnum.ERROR.getCode(), GlobalCodeEnum.ERROR.getMessage(), userMessage, "");
        return globalEntity;
    }

    // error-all
    public static <T> GlobalResponse<T> error(T data, String code, String message, String userMessage, String businessMessage)
    {
        GlobalResponse globalEntity = new GlobalResponse(data, code, message, userMessage, businessMessage);
        return globalEntity;
    }

}
