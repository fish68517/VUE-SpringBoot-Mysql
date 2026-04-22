package com.powerinspection.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex, HttpServletResponse response) {
        response.setStatus(400);
        return ApiResponse.fail(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex, HttpServletResponse response) {
        response.setStatus(400);
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("参数校验失败");
        return ApiResponse.fail(message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex, HttpServletResponse response) {
        response.setStatus(500);
        return ApiResponse.fail(ex.getMessage());
    }
}
