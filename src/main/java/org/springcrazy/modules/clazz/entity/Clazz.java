package org.springcrazy.modules.clazz.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springcrazy.common.pojo.AutoFillEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 班级
 * </p>
 *
 * @author 541720500@qq.com
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Class对象", description = "班级")
@TableName("class")
public class Clazz extends AutoFillEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "班级名")
    private String name;

    @ApiModelProperty(value = "课程")
    private Long courseId;

    @ApiModelProperty(value = "教室")
    private Long classroomId;

    @ApiModelProperty(value = "班主任")
    private Long teacherId;
    @ApiModelProperty(value = "班主任")
    private String teacherName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否结业")
    private Boolean beOver;

    @ApiModelProperty(value = "完结时间")
    private LocalDateTime overTime;

    @ApiModelProperty(value = "完结操作人")
    private Long overOperator;

    @ApiModelProperty(value = "计划开课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @ApiModelProperty(value = "计划结课日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @ApiModelProperty(value = "预排课次数")
    private Integer plannedLessonCount;

    @ApiModelProperty(value = "预招人数")
    private Integer plannedStudentCount;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

    @ApiModelProperty(value = "创建者所属学校id")
    private Long schoolId;

    @ApiModelProperty(value = "年级")
    private Long gradeId;

    @TableField(exist = false)
    private String gradeName;
}
