package org.xiaoxingbomei.advice;


import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xiaoxingbomei.Enum.CommonCodeEnum;
import org.xiaoxingbomei.entity.response.ResponseEntity;
import org.xiaoxingbomei.utils.Exception_Utils;



/**
 * 全局异常处理
 */
//@EqualsAndHashCode
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler
{

    // 拦截：其它所有异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity handlerException(Exception e)
    {
        Exception_Utils.recursiveReversePrintStackCauseCommon(e);
        return ResponseEntity.error(null, CommonCodeEnum.ERROR.getCode(), e.getMessage(), "Exception,当前功能不可用，请稍后再试", "");
    }

}
