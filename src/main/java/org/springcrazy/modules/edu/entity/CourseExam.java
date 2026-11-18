
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 章节测验
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Data
@TableName("edu_course_exam")
@ApiModel(value = "CourseExam对象", description = "随堂测验信息")
public class CourseExam implements Serializable {

  private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;

  /**
   * 资料名称
   */
  @ApiModelProperty(value = "资料名称")
  private String name;

  /**
   * 课程id
   */
  @ApiModelProperty(value = "课程id")
  private Integer courseId;

  /**
   * 章节id
   */
  @ApiModelProperty(value = "章节id")
  private Integer kpointId;


  /**
   * 播放次数
   */
  @ApiModelProperty(value = "文件路径")
  private Integer examId;

  @ApiModelProperty(value = "文件路径 1随堂测验")
  private String examType;

  @ApiModelProperty(value = "详情")
  private String examName;


  /**
   * 显示排序
   */
  @ApiModelProperty(value = "显示排序")
  private Integer sort;

  /**
   * 添加时间
   */
  @ApiModelProperty(value = "添加时间")
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;


}
