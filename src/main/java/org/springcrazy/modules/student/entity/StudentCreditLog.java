package org.springcrazy.modules.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.common.enums.StudentCreditChangeTypeEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生积分变动记录
 * @TableName student_credit_log
 */
@TableName(value ="student_credit_log")
@Data
public class StudentCreditLog implements Serializable {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "学生id")
    @TableField(value = "student_id")
    private Integer studentId;

    @ApiModelProperty(value = "学生账号id")
    @TableField(value = "user_id")
    private Integer userId;

    @ApiModelProperty(value = "变动数量")
    @TableField(value = "credit")
    private Integer credit;

    @ApiModelProperty(value = "剩余积分数量")
    @TableField(value = "current_credit")
    private Integer currentCredit;

    @ApiModelProperty(value = "变动类型")
    @TableField(value = "change_type")
    private StudentCreditChangeTypeEnum changeType;

    @ApiModelProperty(value = "来源id")
    @TableField(value = "source_id")
    private Long sourceId;

    @ApiModelProperty(value = "调整人")
    @TableField(value = "staff_id")
    private Long staffId;

    @ApiModelProperty(value = "变动时间")
    @TableField(value = "add_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime addTime;

    @ApiModelProperty(value = "其他说明")
    @TableField(value = "remark")
    private String remark;

    @ApiModelProperty(value = "创建者所属学校ID")
    private Long schoolId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}