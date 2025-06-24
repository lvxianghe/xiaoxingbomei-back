package org.xiaoxingbomei.common.entity.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlobalRequestContext
{
    // 请求id
    private String ip;

    // 请求url
    private String url;

    // 请求类型 get/post/delete/put
    private String methodType;

    // 请求类
    private String className;

    // 请求方法
    private String methodName;

    // 请求参数
    private Object[] args;

}
