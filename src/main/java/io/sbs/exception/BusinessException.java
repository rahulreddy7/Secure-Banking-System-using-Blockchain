/*
* @date - 2020-03-16
* @authors - Anay Paul, Nanqiao Chen
*/

package io.sbs.exception;


public class BusinessException extends RuntimeException {

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
    }

}
