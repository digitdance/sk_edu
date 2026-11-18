package org.springcrazy.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerifyStateEnum implements BaseEnum {
    APPLY(1, "待审核"),
    APPROVE(2, "审核通过"),
    REJECT(-1, "已驳回");

    @EnumValue
    private final int code;
    @JsonValue
    private final String name;

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    private VerifyStateEnum(final int code, final String name) {
        this.code = code;
        this.name = name;
    }
}
