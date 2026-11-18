package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springcrazy.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 签到状态
 * */
@Getter
@AllArgsConstructor
public enum SignTypeEnum implements BaseEnum {
    TEACHER(1, "老师点名"),
    STUDENT(2, "学员端签到");
    @EnumValue
    private final int code;
    @JsonValue
    private final String name;
}
