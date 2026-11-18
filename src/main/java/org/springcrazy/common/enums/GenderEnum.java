package org.springcrazy.common.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderEnum implements BaseEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

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

    private GenderEnum(final int code, final String name) {
        this.code = code;
        this.name = name;
    }
}
