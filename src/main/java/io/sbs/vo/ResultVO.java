package io.sbs.vo;

import io.sbs.constant.ResultStatus;

import java.io.Serializable;

/**
 * @date 2020-03-16
 * Author : Nanqiao Chen ,Anay Paul
 */
public class ResultVO<T> implements Serializable {

    private T data;
    private String code;
    private String msg;

    private ResultVO(T data, String code) {
        this.data = data;
        this.code = code;
    }
    private ResultVO(T data, String code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public boolean isSuccess() {
        return ResultStatus.SUCCESS.getCode().equals(this.code);
    }

    public static ResultVO createSuccess() {
        return new ResultVO(null, ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMsg());
    }
    public static <T> ResultVO createSuccess(T data) {
        return new ResultVO(data, ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.getMsg());
    }
    public static <T> ResultVO createSuccess(T data, String msg) {
        return new ResultVO(data, ResultStatus.SUCCESS.getCode(), msg);
    }
    public static ResultVO createError(String msg) {
        return new ResultVO(null, ResultStatus.ERROR.getCode(), msg);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
