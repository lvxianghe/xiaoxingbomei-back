package org.xiaoxingbomei.aspect;


import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xiaoxingbomei.entity.request.GlobalRequestContext;

import java.util.Arrays;

/**
 * 统一日志处理：Controller层 通过工程路径切面实现
 *
 * 在微服务中，为了更好的了解接口的运行状态，我们经常会通过日志查看接口的入参、出参、调用地址、调用方法和接口的响应时间。
 * 通过AOP面向切面编程，零侵入完成对接口信息的监控
 * 从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率。
 *
 * `@Aspect`:作用是把当前类标识为一个切面供容器读取
 *
 * `@Pointcut`：Pointcut是植入Advice的触发条件。每个Pointcut的定义包括2部分，一是表达式，二是方法签名
 *
 * `@Around`：环绕增强，相当于MethodInterceptor
 *
 * `@AfterReturning`：后置增强，相当于AfterReturningAdvice，方法正常退出时执行
 *
 * `@Before`：标识一个前置增强方法，相当于BeforeAdvice的功能，相似功能的还有
 *
 * `@AfterThrowing`：异常抛出增强，相当于ThrowsAdvice
 *
 * `@After`: final增强，不管是抛出异常或者正常退出都会执行
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspectByPath
{

    // 如果是需要子线程集成父线程的上下文 可以直接使用 InheritableThreadLocal
//    private static final InheritableThreadLocal<GlobalRequestContext> requestContext = new InheritableThreadLocal<>();
    private static final ThreadLocal<GlobalRequestContext> requestContext = new ThreadLocal<>();

    /**
     * 接口响应时间
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 切入点
     */
    @Pointcut("execution(public * org.xiaoxingbomei.controller..*.*(..))")
    public void ControllerLogAspectByPath() {}

    /**
     * before
     * 1、记录请求信息
     * 2、初始化资源
     */
    @Before("ControllerLogAspectByPath()")
    public void doBefore(JoinPoint joinPoint) throws Throwable
    {
        // 记录接口的起始时间
        startTime.set(System.currentTimeMillis());

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String requestIp    = request.getRemoteAddr();
        String requestUrl   = request.getRequestURL().toString();
        String requestMethod = request.getMethod();
        String requestClassName = joinPoint.getSignature().getDeclaringTypeName();
        String requestMethodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        GlobalRequestContext globalRequestContext = new GlobalRequestContext();
        globalRequestContext.setIp(requestIp);
        globalRequestContext.setUrl(requestUrl);
        globalRequestContext.setMethodType(requestMethod);
        globalRequestContext.setClassName(requestClassName);
        globalRequestContext.setMethodName(requestMethodName);
        globalRequestContext.setArgs(args);

        // 将当前的请求信息设置进threadlocal中
        requestContext.set(globalRequestContext);

    }


    /**
     * around
     * 1、清理资源（暂无）
     * 2、记录方法结束状态
     */
    @After("ControllerLogAspectByPath()")
    public void after(JoinPoint joinPoint) throws Throwable
    {
        // 清理threadlocal变量
        requestContext.remove();
    }

    /**
     * after returning（输出返回参数及处理时间）
     * 1、记录返回值
     * 2、处理返回值
     */
    @AfterReturning(returning = "ret", pointcut = "ControllerLogAspectByPath()")
    public void doAfterReturning(JoinPoint joinPoint,Object ret) throws Throwable
    {

        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容 after returning
        log.info("\n----------------------------------------------------------\n\t{}{}{}{}{}{}{}{}{}{}",
                " << controller after returning aspect info >>",
                "\n\t【request IP】   : \t" + request.getRemoteAddr(),
                "\n\t【request url】  : \t" + request.getRequestURL().toString(),
                "\n\t【http method】  : \t" + request.getMethod(),
                "\n\t【class path】   : \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                "\n\t【spend time】   : \t" + (System.currentTimeMillis() - startTime.get())+ "ms",
                "\n\t【request data】 : \t" + Arrays.toString(joinPoint.getArgs()),
                "\n\t【response data】: \t" + ret,
                "\n\t【response json】: \t" + JSON.toJSONString(ret),
                "\n----------------------------------------------------------\n");

    }


    public static GlobalRequestContext getGlobalRequestContext()
    {
        return requestContext.get();
    }

    public static void setGlobalRequestContext(GlobalRequestContext context)
    {
        requestContext.set(context);
    }

    public static void clearGlobalRequestContext()
    {
        requestContext.remove();
    }

}