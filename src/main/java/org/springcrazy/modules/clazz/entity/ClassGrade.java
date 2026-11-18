package org.springcrazy.modules.clazz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.common.pojo.AutoFillEntity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 年级表
 */
@TableName(value ="class_grade")
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ClassGrade对象", description = "年级表")
public class ClassGrade extends AutoFillEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "未输入名称")
    @ApiModelProperty(value = "年级名称")
    private String name;

    @ApiModelProperty(value = "入学年份")
    private Integer year;

    @ApiModelProperty(value = "排序")
    private Integer sortNum;

    @ApiModelProperty(value = "创建者所属学校id")
    private Long schoolId;

    @ApiModelProperty(value = "年级组长")
    private Long teacherId;

    @ApiModelProperty(value = "备注")
    private String remark;



}