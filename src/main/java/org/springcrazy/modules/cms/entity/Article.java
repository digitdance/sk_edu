
package org.springcrazy.modules.cms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.core.mp.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * 文章信息表实体类
 *
 * @author TongZhou
 * @since 2020-02-28
 */
@Data
@TableName("cms_article")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Article对象", description = "文章信息表")
public class Article extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 文章id
     */
    @ApiModelProperty(value = "文章id")
    @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
    /**
     * 文章标题
     */
    @ApiModelProperty(value = "文章标题")
    private String title;
    /**
     * 文章简介
     */
    @ApiModelProperty(value = "文章简介")
    private String summary;
  /**
   * 文章内容
   */
  @ApiModelProperty(value = "文章内容")
  private String content;
    /**
     * 文章图片url
     */
    @ApiModelProperty(value = "文章图片url")
    private String imageUrl;

    @ApiModelProperty(value = "文章图片url")
    private String images;


    /**
     * 文章发布时间
     */
    @ApiModelProperty(value = "文章发布时间")
    private LocalDateTime publishTime;
    /**
     * 文章点击量
     */
    @ApiModelProperty(value = "文章点击量")
    private Integer clickNum;

    private Integer likes;//喜欢
    private Integer commentsNum;//评论

    /**
     * 点赞数量
     */
    @ApiModelProperty(value = "点赞数量")
    private Integer praiseCount;
    /**
     * 资讯类型（lineNotice 线下课公告 lineExam线下课考试资讯 lineArticle线下课资讯 article网校资讯 webNotice首页公告）
     */
    @ApiModelProperty(value = "资讯类型（lineNotice 线下课公告 lineExam线下课考试资讯 lineArticle线下课资讯 article网校资讯 webNotice首页公告） advertise广告）")
    private String type;
    /**
     * 专业分类id
     */
    @ApiModelProperty(value = "专业分类id")
    private Integer subjectId;
    /**
     * 评论数
     */
    @ApiModelProperty(value = "评论数")
    private Integer commentNum;
    /**
     * 排序值
     */
    @ApiModelProperty(value = "排序值")
    private Integer sort;

    @ApiModelProperty(value = "任务")
    private Integer taskId;

    @ApiModelProperty(value = "用户")
    private Integer userId;

    @ApiModelProperty(value = "学生")
    private Integer studentId;

  @ApiModelProperty(value = "学校")
  private Integer schoolId;

  private Integer taskRecordId;
  private Integer isDraft;//是否是草稿
  private Integer isTop;

    private String url;//广告跳转
   private String abcimg;//信息流样式

  private Integer click;//点击量
  private Integer comments;//评论

}
