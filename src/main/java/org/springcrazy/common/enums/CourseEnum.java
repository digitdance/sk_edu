package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据权限类型
 * 数据权限实际上就是通过拼接sql语句实现的，查询个人的用defaultField 字段 查询机构的用defaultOrgField 字段，具体实现见：
 * com.hzb.erp.api.base.dataScope.DataScopeInterceptor#makeCondition(java.lang.String, java.lang.String)
 *
 * 枚举中的表是可以受数据权限管控的，如果需要自行增加即可
 */
@Getter
@AllArgsConstructor
public enum CourseEnum implements BaseEnum {
    COURSE(1, "录播课"),
    LIVE(2, "直播课"),
    PACKAGE(3, "套餐");
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



}
