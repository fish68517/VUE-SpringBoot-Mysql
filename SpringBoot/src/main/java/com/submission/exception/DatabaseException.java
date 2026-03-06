package com.submission.exception;

/**
 * Database Exception - Custom exception for database operation errors
 */
public class DatabaseException extends RuntimeException {
    private int code;

    public DatabaseException(String message) {
        super(message);
        this.code = 500;
    }

    public DatabaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }

    public DatabaseException(int code, String message, Throwable cause) {
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
