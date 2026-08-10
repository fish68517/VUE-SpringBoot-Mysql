package com.charging.platform.exception;

import com.charging.platform.common.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        return Result.failure(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<Void> handleValidationException(Exception exception) {
        String message;
        if (exception instanceof MethodArgumentNotValidException validException) {
            message = validException.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        } else {
            message = ((BindException) exception).getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }
        return Result.failure(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException exception) {
        return Result.failure(400, exception.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleMessageNotReadable() {
        return Result.failure(400, "请求参数格式不正确");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.warn("数据约束校验失败", exception);
        return Result.failure(400, "数据重复、必填项缺失或关联数据不正确");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        log.error("未处理的服务器异常", exception);
        return Result.failure(500, "服务器处理失败，请稍后重试");
    }
}
