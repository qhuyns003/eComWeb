package com.ecomweb.identity_service.exception;

public class AppException extends RuntimeException {



    private  ErrorCode errorCode;
    private  Object[] args;

    // Constructor có args động
    public AppException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    // Constructor KHÔNG có args (dùng khi không cần truyền tham số động)
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = new Object[0]; // hoặc null cũng được, nhưng [] an toàn hơn
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args; }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
