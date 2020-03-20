package io.sbs.constant;

/**
 * @date 2020-03-16
 * Author : Nanqiao Chen, Anay Paul
 */
public enum ResultStatus {

    SUCCESS("0000", "oporation success"), ERROR("9999", "oporation failed"),
    AlreadyLoggedIn("1111","User is already logged in");
    private String code;
    private String msg;

    private ResultStatus(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
    public String getCode() {
        return code;
    }

}
