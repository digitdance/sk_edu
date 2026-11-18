package org.springcrazy.modules.book.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.common.pojo.AutoFillEntity;
import org.springcrazy.core.mp.base.BaseEntity;

/**
 * <p>
 * 作业
 * </p>
 *
 * @author Ryan
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Homework对象", description = "作业")
public class Book extends BaseEntity {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "分类")
    private Long subjectId;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "封面")
    private String cover;

    @ApiModelProperty(value = "PDF地址")
    private String fileUrl;

    @ApiModelProperty(value = "学校")
    private String schoolId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "点击")
    private Integer click;
    @ApiModelProperty(value = "评论字数")
    private Integer word;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;
}
