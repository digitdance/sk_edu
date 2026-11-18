package org.springcrazy.modules.edu.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.core.mp.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 作业
 * </p>
 *
 * @author Ryan
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Homework对象", description = "喜欢")
public class ScoreRecord extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "课程")
    private Integer courseId;

    @ApiModelProperty(value = "班级")
    private Integer clazzId;

    @ApiModelProperty(value = "用户")
    private Integer userId;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "创建者所属机构")
    private BigDecimal score;

    @ApiModelProperty(value = "总分")
    @TableField(exist = false)
    private BigDecimal sumScore;


    @ApiModelProperty(value = "测验成绩")
    @TableField(exist = false)
    private String tExam;

    @ApiModelProperty(value = "课堂成绩")
    @TableField(exist = false)
    private String tKpointStudy;

    @ApiModelProperty(value = "实训成绩")
    @TableField(exist = false)
    private String tShixun;



    @ApiModelProperty(value = "创建者所属机构")
    private Integer bizId;

    @TableField(exist = false)
    private String scores;


    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "创建者所属机构")
    private BigDecimal quanzhong;


    @ApiModelProperty(value = "实训成绩")
    @TableField(exist = false)
    private String showName;

    @ApiModelProperty(value = "测验成绩")
    @TableField(exist = false)
    private String examScore;

    @ApiModelProperty(value = "课堂成绩")
    @TableField(exist = false)
    private String kpointStudyScore;

    @ApiModelProperty(value = "实训成绩")
    @TableField(exist = false)
    private String shixunScore;

    @ApiModelProperty(value = "班级名称")
    @TableField(exist = false)
    private String clazzName;
}
