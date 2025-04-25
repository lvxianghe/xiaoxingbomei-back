package org.xiaoxingbomei.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 异常工具类
 * 1、默认参数进行倒打堆栈
 * 2、倒打堆栈
 */
@Component
@Slf4j
public class Exception_Utils
{
    // ForwardCounter 类定义
    public static class ForwardCounter
    {
        public int i = 0;
    }

    /**
     * 1、默认参数进行倒打堆栈
     */
    public static void recursiveReversePrintStackCauseCommon(Throwable t)
    {
        StringBuilder sb        = new StringBuilder();  //
        ForwardCounter counter  = new ForwardCounter(); //
        int causeDepth = 15;     // 递归打印的cause的最大深度
        int stackDepth = 15;     // 每一个异常栈的打印深度
        recursiveReversePrintStackCause(t,causeDepth,counter,stackDepth,sb);
        log.error("\n---------------------reverse exception stackTrace begin---------------------\n");
        log.error("\n" +sb.toString());
        log.error("\n---------------------reverse exception stackTrace end---------------------\n");
    }

    /**
     * 2、倒打堆栈：递归逆向打印堆栈及cause(即从最底层的异常开始往上打)
     * @param t 原始异常
     * @param causeDepth 需要递归打印的cause的最大深度
     * @param counter 当前打印的cause的深度计数器(这里必须用引用类型，如果用基本数据类型，你对计数器的修改只能对当前栈帧可见，但是这个计数器，又必须在所有栈帧中可见，所以只能用引用类型)
     * @param stackDepth 每一个异常栈的打印深度
     * @param sb 字符串构造器
     */
    public static void recursiveReversePrintStackCause(Throwable t, int causeDepth, ForwardCounter counter, int stackDepth, StringBuilder sb)
    {
        if(t == null)
        {
            return;
        }
        if (t.getCause() != null)
        {
            recursiveReversePrintStackCause(t.getCause(), causeDepth, counter, stackDepth, sb);
        }
        if(counter.i++ < causeDepth){
            doPrintStack(t, stackDepth, sb);
        }
    }

    // 处理堆栈信息
    public static void doPrintStack(Throwable t, int stackDepth, StringBuilder sb)
    {
        StackTraceElement[] stackTraceElements = t.getStackTrace();
        if(sb.lastIndexOf("\t") > -1)
        {
            sb.deleteCharAt(sb.length()-1);
            sb.append("Caused: ");
        }
        sb.append(t.getClass().getName()).append(": ").append(t.getMessage()).append("\n\t");
        for(int i=0; i < stackDepth; ++i)
        {
            if(i >= stackTraceElements.length){
                break;
            }
            StackTraceElement element = stackTraceElements[i];
             sb.append(reduceClassName(element.getClassName()))
             // sb.append(element.getClassName())
                    .append("[")
                    .append(element.getMethodName())
                    .append(":")
                    .append(element.getLineNumber())
                    .append("]")
                    .append("\n\t");
        }
    }

    // 简化类名以便更好地阅读
    private static String reduceClassName(String className)
    {
        String[] parts = className.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++)
        {
            sb.append(parts[i].charAt(0)).append(".");
        }
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }

}
