package com.agricultural.exception;

/**
 * 数据访问异常类
 * 用于处理数据库操作中的异常情况
 */
public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    public DataAccessException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public DataAccessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.message = message;
    }

    public DataAccessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
