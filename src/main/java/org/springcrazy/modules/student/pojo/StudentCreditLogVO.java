package org.springcrazy.modules.student.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.student.entity.StudentCreditLog;

/**
 * 兑换记录
 */
@Data
public class StudentCreditLogVO extends StudentCreditLog {

    @ApiModelProperty(value = "学生姓名")
    private String studentName;

}
