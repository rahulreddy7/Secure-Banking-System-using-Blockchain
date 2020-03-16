/*
* Author : Nanqiao Chen, Anay Paul
*/

package io.sbs.web.advice;

import io.sbs.exception.BusinessException;
import io.sbs.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ControllerExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO catchBusinessException(BusinessException e) {
        logger.error("operation failed：", e);
        return ResultVO.createError(e.getMessage());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO catchException(Exception e) {
        logger.error("operation failed：", e);
        return ResultVO.createError(e.getMessage());
    }

}
