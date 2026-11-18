package org.springcrazy.common.enums;


public enum ResponseCodeEnums {
    SUCCESS(1000, "操作成功"),
    LOGIN_SUCCESS(1001, "注销成功"),
    LOGOUT_SUCCESS(1002, "注销成功"),
    BIZ_ERROR(2000, "业务异常"),
    TOKEN_NOTFOUND(2001, "用户未登录"),
    TOKEN_EXPIRED(2002, "登录状态丢失，请重新登录"),
    LOGIN_ERROR(2003, "验证登录失败"),
    FORBIDDEN(2100, "权限不足"),
    BLANK_PARAMS_ERROR(2101, "空参数"),
    PARAMS_FORMAT_ERROR(2102, "参数格式错误"),
    SYSTEM_ERROR(2200, "系统错误");

    private int code;
    private String msg;

    private ResponseCodeEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
