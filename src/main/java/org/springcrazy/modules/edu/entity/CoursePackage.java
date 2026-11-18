
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 实体类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@Data
@TableName("edu_course_package")
@ApiModel(value = "CoursePackage对象", description = "CoursePackage对象")
public class CoursePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 主课程id
     */
    @ApiModelProperty(value = "主课程id")
    private Integer parentCourseId;
    /**
     * 包含的课程id
     */
    @ApiModelProperty(value = "包含的课程id")
    private Integer courseId;
    /**
     * 课程套餐排序值
     */
    @ApiModelProperty(value = "课程套餐排序值")
    private Integer sort;


  @ApiModelProperty(value = "课程完结状态 1未开始 2进行中 3已完结 4学期完结存档")
  private String completionStatus;

    @ApiModelProperty(value = "到期时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected LocalDateTime completionTime;

  private Integer teacherId;
  private String teacherIds;
  private String teachers;//授课老师
  private String password;


    private Integer isDelete;

}
