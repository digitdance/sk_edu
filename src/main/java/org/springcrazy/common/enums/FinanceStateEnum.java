package org.springcrazy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springcrazy.common.constant.CommonConst;
import org.springcrazy.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 财务认款状态类型
 */
@Getter
@AllArgsConstructor
public enum FinanceStateEnum implements BaseEnum {

    /**
     * 注意该枚举各项和必须和 VerifyStateEnum 的code值一致.
     */
    APPLY(CommonConst.VERIFY_READY_CODE, "审核中"),
    PASS(CommonConst.VERIFY_PASS_CODE, "已审核"),
    REJECT(CommonConst.VERIFY_REJECT_CODE, "已拒绝");

    @EnumValue
    private final int code;
    @JsonValue
    private final String name;
}
