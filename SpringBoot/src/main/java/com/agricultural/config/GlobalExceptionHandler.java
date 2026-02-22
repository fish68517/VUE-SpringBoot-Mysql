package com.agricultural.config;

import com.agricultural.dto.Result;
import com.agricultural.exception.BusinessException;
import com.agricultural.exception.DataAccessException;
import com.agricultural.util.LoggerUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理应用中的所有异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleBusinessException(BusinessException ex, WebRequest request) {
        LoggerUtil.error("业务异常: " + ex.getMessage(), ex);
        return ResponseEntity.ok(Result.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleDataAccessException(DataAccessException ex, WebRequest request) {
        LoggerUtil.error("数据访问异常: " + ex.getMessage(), ex);
        return ResponseEntity.ok(Result.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        String message = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        LoggerUtil.warn("参数验证失败: " + message);
        return ResponseEntity.ok(Result.validationError(message));
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, WebRequest request) {
        LoggerUtil.warn("请求路径不存在: " + ex.getRequestURL());
        return ResponseEntity.ok(Result.notFound("请求的资源不存在"));
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleException(Exception ex, WebRequest request) {
        LoggerUtil.error("系统异常: " + ex.getMessage(), ex);
        return ResponseEntity.ok(Result.error(500, "系统内部错误，请稍后重试"));
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Result<?>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        LoggerUtil.error("运行时异常: " + ex.getMessage(), ex);
        return ResponseEntity.ok(Result.error(500, "系统内部错误，请稍后重试"));
    }
}
