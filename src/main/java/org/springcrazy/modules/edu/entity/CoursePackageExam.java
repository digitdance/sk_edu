
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@Data
@TableName("edu_course_package_exam")
@ApiModel(value = "CoursePackage对象", description = "CoursePackage对象")
public class CoursePackageExam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 主课程id
     */
    @ApiModelProperty(value = "班级")
    private Integer clazzId;

    @ApiModelProperty(value = "课程")
    private Integer courseId;

    @ApiModelProperty(value = "章节")
    private Integer kpointId;

    /**
     * 包含的课程id
     */
    @ApiModelProperty(value = "考试")
    private Integer exampaperId;


    @ApiModelProperty(value = "考试名称")
    private String customExampaperName;

    /**
     * 课程套餐排序值
     */
    @ApiModelProperty(value = "课程套餐排序值")
    private Integer sort;


      @ApiModelProperty(value = "课程完结状态 1未开始 2进行中 3已完结 4学期完结存档")
      private String completionStatus;


    @ApiModelProperty(value = "0未开始 1进行中 -1已失效")
    private String status;


    @ApiModelProperty(value = "到期时间")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    protected LocalDateTime completionTime;

   private Integer teacherId;
   private String teacherIds;
   private String teachers;//授课老师
   private String password;

   @TableField(value = "type")
   private Integer examType;
   private Integer isDelete;


    private String examTime;
    private Boolean canRepeat;
    private Boolean antiCheating;


}
