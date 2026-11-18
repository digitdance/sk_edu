package org.springcrazy.modules.student.pojo;

import lombok.Data;
import org.springcrazy.common.enums.StudentCreditChangeTypeEnum;

import javax.validation.constraints.NotNull;

@Data
public class StudentCreditChangeParamDTO {
    @NotNull(message = "缺少参数")
    private Long studentId;
    @NotNull(message = "请输入积分")
    private Integer credit;
    private String remark;
    // 调整人
    private Long staffId;
    private StudentCreditChangeTypeEnum type;
}
