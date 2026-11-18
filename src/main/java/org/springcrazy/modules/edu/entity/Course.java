
package org.springcrazy.modules.edu.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.jna.platform.unix.solaris.LibKstat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.common.enums.CourseEnum;
import org.springcrazy.core.json.SerializerBigDecimal;

/**
 * 课程表实体类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Data
@TableName("edu_course")
@ApiModel(value = "Course对象", description = "课程表")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程主键
     */
    @ApiModelProperty(value = "课程主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;
    /**
     * 1上架2下架3删除
     */
    @ApiModelProperty(value = "1上架2下架3删除")
    private Integer isAvaliable;
    /**
     * 课程专业id
     */
    @ApiModelProperty(value = "课程专业id")
    private Integer subjectId;
    /**
     * 添加时间
     */
    @ApiModelProperty(value = "添加时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "添加人")
    private Integer creater;

    @ApiModelProperty(value = "是否模板课程")
    private Integer isTemplateCourse;

    @ApiModelProperty(value = "添加人")
    private Integer updater;
    /**
     * 课程原价格（只显示）
     */
    @ApiModelProperty(value = "课程原价格（只显示）")
    @JsonSerialize(using = SerializerBigDecimal.class)
    private BigDecimal sourcePrice;
    /**
     * 课程销售价格（实际支付价格）设置为0则可免费观看
     */
    @ApiModelProperty(value = "课程销售价格（实际支付价格）设置为0则可免费观看")
    @JsonSerialize(using = SerializerBigDecimal.class)
    private BigDecimal currentPrice;

      @ApiModelProperty(value = "课程销售价格（实际支付价格）设置为0则可免费观看")
      @JsonSerialize(using = SerializerBigDecimal.class)
      private BigDecimal unitPrice;

    /**
     * 课程简介
     */
    @ApiModelProperty(value = "课程简介")
    private String title;
    /**
     * 课程详情
     */
    @ApiModelProperty(value = "课程详情")
    private String context;
    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String logo;
    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    /**
     * 销售数量
     */
    @ApiModelProperty(value = "销售数量")
    private Integer pageBuycount;
    /**
     * 假销售数
     */
    @ApiModelProperty(value = "假销售数")
    @TableField("bogusBuycount")
    private Integer bogusBuycount;
    /**
     * 评论数
     */
    @ApiModelProperty(value = "评论数")
    @TableField("commentNum")
    private Integer commentNum;
    /**
     * 浏览数量
     */
    @ApiModelProperty(value = "浏览数量")
    private Integer pageViewcount;
    /**
     * 有效结束时间
     */
    @ApiModelProperty(value = "有效结束时间")
    private Date endTime;
    /**
     * 有效期类型，0：到期时间，1：按天数
     */
    @ApiModelProperty(value = "有效期类型，0：到期时间，1：按天数")
    private String losetype;
    /**
     * 有效期:商品订单过期时间点
     */
    @ApiModelProperty(value = "有效期:商品订单过期时间点")
    private String loseTime;
    /**
     * 序列
     */
    @ApiModelProperty(value = "序列")
    private Integer sort;
    /**
     * course 课程类型：COURSE(劳动课) LIVE(直播) PACKAGE(套餐) SHIJIAN(实践课)
     */
    @ApiModelProperty(value = "course 课程类型：COURSE(录播) LIVE(直播) PACKAGE(套餐) LABURE(劳动课) SHIJIAN(实践课) exam(考试课)")
    private String sellType;

    @ApiModelProperty(value = " course(普通课) exam(考试课)")
    private String type;
    /**
     * 直播开始时间
     */
    @ApiModelProperty(value = "直播开始时间")
    private Date liveBeginTime;
    /**
     * 直播结束时间
     */
    @ApiModelProperty(value = "直播结束时间")
    private Date liveEndTime;
    /**
     * 假浏览量
     */
    @ApiModelProperty(value = "假浏览量")
    @TableField("bogusViewcount")
    private Integer bogusViewcount;

    /**
     * 假学习人数
     */
    @ApiModelProperty(value = "假学习人数")
    @TableField("bogusStudycount")
    private Integer bogusStudycount;

    /**
     * 讲师id
     */
    @ApiModelProperty(value = "讲师id")
    private Integer teacherId;

    @ApiModelProperty(value = "管理員id")
    private Integer managerId;



    @ApiModelProperty(value = "班主任")
    private Integer mainTeacherId;


    @TableLogic
    @ApiModelProperty("是否已删除")
    private Integer isDeleted;


    /**
     * 面授课的开始时间
     */
    @ApiModelProperty(value = "面授课的开始时间")
    private Date faceTeachingTime;

    /**
     * 面授课上课地点关联subjectid
     */
    @ApiModelProperty(value = "面授课上课地点关联subjectid")
    private Integer faceTeachingSubject;


    /**
     * 面授课上课地址
     */
    @ApiModelProperty(value = "面授课上课地址")
    private String faceTeachingAddress;


    /**
     * 面授课课程安排
     */
    @ApiModelProperty(value = "面授课课程安排")
    private String faceTeachingContext;

    /**
     * 面授课联系电话
     */
    @ApiModelProperty(value = "面授课联系电话")
    private String faceTeachingMobile;

    @ApiModelProperty(value = "年级")
    private String grade;

    @ApiModelProperty(value = "学校")
    @TableField(fill = FieldFill.INSERT)
    private Long schoolId;

    @ApiModelProperty(value = "授课密码")
    private String password;

    @ApiModelProperty(value = "授课教师")
    private String teacherIds;

    @ApiModelProperty(value = "授课教师")
    @TableField(exist = false)
    private Integer value;

    @ApiModelProperty(value = "资料")
    private String materialIds;

    @TableField(exist = false)
    private String mine;

    @TableField(exist = false)
    private String templateCourse;

    private String scoreSetting;

    @ApiModelProperty(value = "copy2ClazzId")
    @TableField(exist = false)
    private Integer copy2ClazzId;

}
