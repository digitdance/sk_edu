package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springcrazy.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 星期
 *
 * @author cui
 */
@Getter
@AllArgsConstructor
public enum TeacherTypeEnum implements BaseEnum {
    TEACHER(1, "上课老师"),
    ASSISTANT(2, "助教");
    @EnumValue
    private final int code;
    @JsonValue
    private final String name;
}
