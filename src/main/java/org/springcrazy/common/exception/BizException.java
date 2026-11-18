package org.springcrazy.common.exception;


import org.springcrazy.common.enums.ResponseCodeEnums;

public class BizException extends RuntimeException {
    private final Integer code;

    public BizException(ResponseCodeEnums type) {
        super(type.getMsg());
        this.code = type.getCode();
    }

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BizException(String msg) {
        super(msg);
        this.code = ResponseCodeEnums.BIZ_ERROR.getCode();
    }

    public Integer getCode() {
        return this.code;
    }
}
