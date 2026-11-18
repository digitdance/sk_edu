package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springcrazy.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 签到状态
 * */
@Getter
@AllArgsConstructor
public enum SignStateEnum implements BaseEnum {
    /**
     * 默认为未签到
     */
    NONE(0, "未签到"),
    NORMAL(1, "已签到"),
    LATE(2, "补签"),
    LEAVE(3, "请假"),
    ABSENT(4, "旷课");
    @EnumValue
    private final int code;
    @JsonValue
    private final String name;
}
