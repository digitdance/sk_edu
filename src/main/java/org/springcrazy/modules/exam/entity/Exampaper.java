
package org.springcrazy.modules.exam.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springcrazy.modules.edu.entity.CoursePackageExam;

/**
 * 考试试卷表实体类
 *
 * @author TongZhou
 * @since 2020-12-01
 */
@Data
@TableName("exam_exampaper")
@ApiModel(value = "Exampaper对象", description = "考试试卷表")
public class Exampaper implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
    /**
     * 专业分类id
     */
    @ApiModelProperty(value = "专业分类id")
    @TableField("subjectId")
    private Integer subjectId;


    @TableField(exist = false)
  private String subjectIds;
    /**
     * 考试名称
     */
    @ApiModelProperty(value = "考试名称")
    private String name;
    /**
     * 试卷描述
     */
    @ApiModelProperty(value = "试卷描述")
    private String info;
    /**
     * 限制答题的时间 单位分钟
     */
    @ApiModelProperty(value = "限制答题的时间 单位分钟")
    @TableField("replyTime")
  private Integer replyTime;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "addTime",fill = FieldFill.INSERT)
  private LocalDateTime addTime;
    /**
     * 状态normal正常(上架)  frozen冻结(下架)  delete删除
     */
    @ApiModelProperty(value = "状态normal正常(上架)  frozen冻结(下架)  delete删除")
    private String status;
    /**
     * 试卷难度 simple简单  commonly中等 difficulty困难
     */
    @ApiModelProperty(value = "试卷难度 simple简单  commonly中等 difficulty困难")
    private String level;
    /**
     * 参加考试人数
     */
    @ApiModelProperty(value = "参加考试人数")
    @TableField("joinNum")
  private Integer joinNum;
    /**
     * 参加考试的平均分数
     */
    @ApiModelProperty(value = "参加考试的平均分数")
    @TableField("avgScore")
    private BigDecimal avgScore;
    /**
     * 试卷类型
     */
    @ApiModelProperty(value = "试卷类型 4 課後練習 3实训")
    private Integer type;
    /**
     * 试题总数
     */
    @ApiModelProperty(value = "试题总数")
    @TableField("qstCount")
  private Integer qstCount;
    /**
     * 最后编辑时间
     */
    @ApiModelProperty(value = "最后编辑时间")
    @TableField(value = "updateTime",fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;
    /**
     * 试卷总分
     */
    @ApiModelProperty(value = "试卷总分")
    private BigDecimal score;
    /**
     * 及格分数比例（总分的百分之几）
     */
    @ApiModelProperty(value = "及格分数比例（总分的百分之几）")
    @TableField("passRate")
  private Double passRate;
    /**
     * 组卷方式 手动manual 智能automatic 随机random
     */
    @ApiModelProperty(value = "组卷方式 手动manual 智能automatic 随机random")
    @TableField("assemblyMode")
    private String assemblyMode;
    /**
     * 试题类型
     */
    @ApiModelProperty(value = "试题类型")
    @TableField("questionsType")
    private String questionsType;
    /**
     * 及格/完成/通关人数
     */
    @ApiModelProperty(value = "及格/完成/通关人数")
    @TableField("passNum")
    private Integer passNum;
    /**
     * 实际参加考试人数（去重）
     */
    @ApiModelProperty(value = "实际参加考试人数（去重）")
    @TableField("actualJoinNum")
    private Integer actualJoinNum;


    @ApiModelProperty(value = "已发布分数")
   // @TableField("has_publish_score")
    @TableField(exist = false)
    private Integer hasPublishScore;


    @ApiModelProperty(value = "是否批改")
    @TableField(exist = false)
    private Integer pigaiStatus;


    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值")
    @TableField("sortNum")
    private Integer sortNum;

    private Integer createrId;
    private String createrName;
    private Integer kpointId;
    private Integer courseId;

    @TableField(exist = false)
    List<PaperMiddle> middles;

    @TableField(exist = false)
    private Integer middleClazz;
    @TableField(exist = false)
    private Integer middleId;
    @TableField(exist = false)
    private String middleName;

    @TableField(exist = false)
    private String clazzIds;

    @TableField(exist = false)
    private String clazzName;

    @TableField(exist = false)
    private String userIds;

    @TableField(exist = false)
    private String courseIds;

    @TableField(exist = false)
    private Integer clazzId;
    @TableField(exist = false)
    private String examName;
    @TableField(exist = false)
    private String examType;

    @TableField(exist = false)
    private String kpointIdNull;

  @TableField(exist = false)
  private String sumScore;

  @TableField(exist = false)
  private String examNum;

  @TableField(exist = false)
  private String pigaiNum;

    @TableField(exist = false)
    private String avg;



    @TableLogic
    @ApiModelProperty("是否已删除")
    private Integer isDeleted;


    @TableField(exist = false)
    private CoursePackageExam coursePackageExam;
}
