package org.xiaoxingbomei.entity.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.N;
import org.xiaoxingbomei.Enum.CommonCodeEnum;
import org.xiaoxingbomei.annotation.NoGlobalEntity;

import java.io.Serializable;

@Data
public class ResponseEntity<T> implements Serializable
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
    public ResponseEntity() {}

    // 有参构造
    public ResponseEntity(T data, String code, String message, String userMessage, String businessMessage)
    {
        this.data = data;
        this.code = code;
        this.message = message;
        this.userMessage = userMessage;
        this.businessMessage = businessMessage;
    }

    // success-data
    public static <T> ResponseEntity<T> success(T data)
    {
        ResponseEntity globalEntity = new ResponseEntity(data, CommonCodeEnum.SUCCESS.getCode(), CommonCodeEnum.SUCCESS.getMessage(), "", "");
        return globalEntity;
    }

    // success-data,businessMessage
    public static <T> ResponseEntity<T> success(T data, String userMessage)
    {
        ResponseEntity globalEntity = new ResponseEntity(data, CommonCodeEnum.SUCCESS.getCode(), CommonCodeEnum.SUCCESS.getMessage(), userMessage, "");
        return globalEntity;
    }

    // error-data
    public ResponseEntity<T> error(T data)
    {
        this.data = data;
        this.code = ERROR;
        return this;
    }

    // success-message
    public static <T> ResponseEntity<T> success(String message)
    {
        ResponseEntity globalEntity = new ResponseEntity(null, CommonCodeEnum.SUCCESS.getCode(), message, message, message);
        return globalEntity;
    }

    // success-all
    public static <T> ResponseEntity<T> success(T data, String code, String message, String userMessage, String businessMessage)
    {
        ResponseEntity globalEntity = new ResponseEntity(data, code, message, userMessage, businessMessage);
        return globalEntity;
    }

    // error-message
    public static <T> ResponseEntity<T> error(String message)
    {
        ResponseEntity globalEntity = new ResponseEntity(null, CommonCodeEnum.ERROR.getCode(), message, message, message);
        return globalEntity;
    }

    // error-data,businessMessage
    public static <T> ResponseEntity<T> error(T data, String userMessage)
    {
        ResponseEntity globalEntity = new ResponseEntity(data, CommonCodeEnum.ERROR.getCode(), CommonCodeEnum.ERROR.getMessage(), userMessage, "");
        return globalEntity;
    }

    // error-all
    public static <T> ResponseEntity<T> error(T data, String code, String message, String userMessage, String businessMessage)
    {
        ResponseEntity globalEntity = new ResponseEntity(data, code, message, userMessage, businessMessage);
        return globalEntity;
    }

}
