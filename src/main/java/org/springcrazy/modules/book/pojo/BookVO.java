package org.springcrazy.modules.book.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 作业
 * </p>
 *
 * @author Ryan
 */
@Data
@ApiModel(value = "Homework对象", description = "作业")
public class BookVO {

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "分类")
    private Integer subjectId;

    @ApiModelProperty(value = "分类")
    private String subjectName;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "描述")
    private String fileUrl;

    @ApiModelProperty(value = "学校")
    private Long schoolId;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "点击")
    private Integer click;
    @ApiModelProperty(value = "评论字数")
    private Integer word;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

    private Integer completeCount;

    @ApiModelProperty(value = "单个学生查询时用于判断是否完成")
    private Boolean completed;

    private List<BookRecordVO> recordList;

    @ApiModelProperty(value = "分类")
    private Integer readCount;
}
