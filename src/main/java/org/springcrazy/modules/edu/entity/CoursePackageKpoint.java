
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 实体类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@Data
@TableName("edu_course_package_kpoint")
@ApiModel(value = "CoursePackage对象", description = "CoursePackage对象")
public class CoursePackageKpoint implements Serializable {

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

    @ApiModelProperty(value = "包含的章节id")
    private Integer kpointId;
    /**
     * 课程套餐排序值
     */
    @ApiModelProperty(value = "课程套餐排序值")
    private Integer sort;


  @ApiModelProperty(value = "课程完结状态 1未开始 2进行中 3已完结 4学期完结存档")
  private String completionStatus;
  private Integer teacherId;
  private String teacherIds;
  private String teachers;//授课老师
  private Integer isOpen;
  private Integer isDelete;

}
