
package org.springcrazy.modules.edu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程播放记录(学习记录)实体类
 *
 * @author TongZhou
 * @since 2020-05-06
 */
@Data
@TableName("edu_point_homeworkhistory")
@ApiModel(value = "CourseStudyhistory对象", description = "课程播放记录(学习记录)")
public class PointHomeworkhistory implements Serializable {

    private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    /**
     * 课程id
     */
    @ApiModelProperty(value = "课程id")
    private Integer courseId;


    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id")
    private Integer kpointId;

  @ApiModelProperty(value = "发布到广场")
  private boolean isPublish;

  @ApiModelProperty(value = "允许评论")
  private boolean canComment;



  @ApiModelProperty(value = "课程名称")
  @TableField(exist = false)
  private String logo;
    /**
     * 课程名称
     */
    @ApiModelProperty(value = "课程名称")
    private String courseName;

  @ApiModelProperty(value = "课程名称")
  private Integer kpointCourseId;

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String kpointName;


  @ApiModelProperty(value = "创建时间")
  private Date createTime;


    /**
     * playercount小于20时记录,备注观看的时间，叠加
     */
    @ApiModelProperty(value = "playercount小于20时记录,备注观看的时间，叠加")
    private String databack;


  @ApiModelProperty(value = "内容")
  private String content;

  @ApiModelProperty(value = "图片")
  private String pics;

  @ApiModelProperty(value = "图片")
  private String attachmentUrl;
    /**
     * 最后观看时间
     */
    @ApiModelProperty(value = "最后观看时间")
    private Date updateTime;

    @ApiModelProperty(value = "作业提交时间")
    private Date doHomeworkTime;

    /**
     * 2 是  1不是
     */
    @ApiModelProperty(value = "2 是  1不是")
    private String complete;

    /**
     * 观看总时长 int
     */
    @ApiModelProperty(value = "观看总时长")
    private int watchTime;

    /**
     * 观看总时长 String
     */
    @ApiModelProperty(value = "观看总时长")
    @TableField(exist = false)
    private String watchStingTime;

    /**
     * 学习进度
     */
    @ApiModelProperty(value = "学习进度")
    private String studyLearning;


  @ApiModelProperty(value = "学习进度")
  private String attachment;

    /**
     * 订单过期时间
     */
    @ApiModelProperty(value = "订单过期时间")
    @TableField(exist = false)
    private Date authTime;
    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
    @TableField(exist = false)
    private Date payTime;

    /**
     * 视频时长
     */
    @ApiModelProperty(value = "视频时长")
    @TableField(exist = false)
    private String videoTime;

    /**
     * 父级id
     */
    @ApiModelProperty(value = "父级id")
    @TableField(exist = false)
    private Integer parentId;

    /**
     * 学习总人数
     */
    @ApiModelProperty(value = "学习总人数")
    @TableField(exist = false)
    private Integer studyPope;


    /**
     * 已学习人数
     */
    @ApiModelProperty(value = "已学习人数")
    @TableField(exist = false)
    private Integer studyPopeShould;

    /**
     * 学习完成人数
     */
    @ApiModelProperty(value = "学习完成人数")
    @TableField(exist = false)
    private Integer studyPopeComplete;

    /**
     * 未学习人数
     */
    @ApiModelProperty(value = "未学习人数")
    @TableField(exist = false)
    private Integer studyPopeNo;

    /**
     * 查询类型
     */
    @ApiModelProperty(value = "查询类型")
    @TableField(exist = false)
    private String popeType;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @TableField(exist = false)
    protected String mobile;

    /**
     * 账户名
     */
    @ApiModelProperty(value = "账户名")
    @TableField(exist = false)
    protected String userName;

    @ApiModelProperty(value = "账户名")
    @TableField(exist = false)
    protected String userAvatar;


    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    @TableField(exist = false)
    protected String showName;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    @TableField(exist = false)
    protected String realName;

    /**
     * 已学的所有用户id
     * */
    @ApiModelProperty(value = "真实姓名")
    @TableField(exist = false)
    protected String userIdList;


    /**
     * 直播开始时间
     */
    @ApiModelProperty(value = "直播开始时间")
    @TableField(exist = false)
    private Date beginsTime;

    /**
     * 直播结束时间
     */
    @ApiModelProperty(value = "直播结束时间")
    @TableField(exist = false)
    private Date endTime;

    /**
     * 查询课程类型
     */
    @ApiModelProperty(value = "查询课程类型")
    private String courseType;

    /**
     * 套餐父级id
     */
//    @ApiModelProperty(value = "套餐父级id")
//    private int kpointCourseId;

    /**
     * 学习类型，1.单课程2.套餐
     */
    @ApiModelProperty(value = "学习类型，1.单课程2.套餐")
    @TableField(exist = false)
    private int studyType;
  /**
   * 套餐课程id
   */
  @ApiModelProperty(value = "套餐课程id")
  @TableField(exist = false)
  private int coursePackageId;
  /**
   * 课程类型 1 点播 2 直播 3 套餐
   */
  @ApiModelProperty(value = "课程类型")
  @TableField(exist = false)
  private Integer sellType;
}
