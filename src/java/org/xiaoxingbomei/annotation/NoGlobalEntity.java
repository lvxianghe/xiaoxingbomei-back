package org.xiaoxingbomei.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不需要包装的返回值
 *
 * - 接口很多每个接口需要封装`GlobalEntity`对象是一个重复工作，可以通过`@RestControllerAdvice`注解组合使用`ResponseBodyAdvice`对接口方法进行扩展
 *
 * - 实现扩展后还要考虑一些接口不需要返回`GlobalEntity`对象，可以通过一个自定义注解`@NoGlobalEntity`排除不要封装的接口
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface NoGlobalEntity
{

}