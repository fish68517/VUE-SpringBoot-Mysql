package com.submission.exception;

/**
 * File Operation Exception - Custom exception for file operation errors
 */
public class FileOperationException extends RuntimeException {
    private int code;

    public FileOperationException(String message) {
        super(message);
        this.code = 400;
    }

    public FileOperationException(int code, String message) {
        super(message);
        this.code = code;
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    public FileOperationException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
