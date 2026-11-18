
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
 * 专题信息表实体类
 *
 * @author TongZhou
 * @since 2020-02-28
 */
@Data
@TableName("cms_special")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Special对象", description = "专题表")
public class Special extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 专题id
     */
    @ApiModelProperty(value = "专题id")
    @TableId(value = "id", type = IdType.AUTO)
  private Integer id;
    /**
     * 专题标题
     */
    @ApiModelProperty(value = "专题标题")
    private String title;
    /**
     * 专题简介
     */
    @ApiModelProperty(value = "专题简介")
    private String summary;
  /**
   * 专题内容
   */
  @ApiModelProperty(value = "专题内容")
  private String content;
    /**
     * 专题图片url
     */
    @ApiModelProperty(value = "专题图片url")
    private String imageUrl;
    /**
     * 专题发布时间
     */
    @ApiModelProperty(value = "专题发布时间")
    private LocalDateTime publishTime;
    /**
     * 专题点击量
     */
    @ApiModelProperty(value = "专题点击量")
    private Integer clickNum;
    /**
     * 点赞数量
     */
    @ApiModelProperty(value = "点赞数量")
    private Integer praiseCount;
    /**
     * 资讯类型（lineNotice 线下课公告 lineExam线下课考试资讯 lineArticle线下课资讯 article网校资讯 webNotice首页公告）
     */
    @ApiModelProperty(value = "资讯类型（lineNotice 线下课公告 lineExam线下课考试资讯 lineArticle线下课资讯 article网校资讯 webNotice首页公告 readtask阅读活动）")
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


  /**
   * 排序值
   */
  @ApiModelProperty(value = "所有文章")
  private String articles;

  /**
   * 排序值
   */
  @ApiModelProperty(value = "所有课程")
  private String courses;



}
