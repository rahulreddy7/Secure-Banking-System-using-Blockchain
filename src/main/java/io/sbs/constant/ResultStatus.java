package io.sbs.constant;

/**
 * @date 2020-03-16
 */
public enum ResultStatus {

    SUCCESS("0000", "oporation success"), ERROR("9999", "oporation failed");

    private String code;
    private String msg;

    private ResultStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }}
