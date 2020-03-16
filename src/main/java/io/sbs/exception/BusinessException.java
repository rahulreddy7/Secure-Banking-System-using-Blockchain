package io.sbs.exception;


public class BusinessException extends RuntimeException {

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
