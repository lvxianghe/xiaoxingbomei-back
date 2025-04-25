//package org.xiaoxingbomei.advice;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//import org.xiaoxingbomei.annotation.NoGlobalEntity;
//import org.xiaoxingbomei.entity.response.ResponseEntity;
//import org.xiaoxingbomei.utils.Exception_Utils;
//
//
///**
// *
// *
// * - 接口很多每个接口需要封装`GlobalEntity`对象是一个重复工作，可以通过`@RestControllerAdvice`注解组合使用`ResponseBodyAdvice`对接口方法进行扩展
// *
// * - 实现扩展后还要考虑一些接口不需要返回`GlobalEntity`对象，可以通过一个自定义注解`@NoGlobalEntity`排除不要封装的接口
// */
//
//@RestControllerAdvice(basePackages = "org.xiaoxingbomei.controller")
//public class ResponseAdvice implements ResponseBodyAdvice<Object>
//{
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        //过滤掉已经加上 GlobalEntity 返回值的方法
//        return !(returnType.getParameterType().equals(ResponseEntity.class)
//                || returnType.hasMethodAnnotation(NoGlobalEntity.class));
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body,
//                                  MethodParameter returnType,
//                                  MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  ServerHttpRequest request,
//                                  ServerHttpResponse response)
//    {
//        //String类型需要特殊处理
//        if (returnType.getParameterType().equals(String.class))
//        {
//            ObjectMapper objectMapper = new ObjectMapper();
//            try
//            {
//                return objectMapper.writeValueAsString(new ResponseEntity<>().success(body));
//            } catch (JsonProcessingException e)
//            {
//                Exception_Utils.recursiveReversePrintStackCauseCommon(e);
//            }
//        }
//        return new ResponseEntity<>().success(body);
//    }
//
//}
