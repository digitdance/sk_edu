package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springcrazy.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum SwitchEnum implements BaseEnum {
    YES(1, "启用"),
    NO(0, "禁用");

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

    private SwitchEnum(final int code, final String name) {
        this.code = code;
        this.name = name;
    }
}
