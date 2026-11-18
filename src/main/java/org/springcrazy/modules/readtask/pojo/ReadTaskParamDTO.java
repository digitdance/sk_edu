package org.springcrazy.modules.readtask.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 作业列表查询DTO
 */
@Data
public class ReadTaskParamDTO extends PaginateDTO {
    private Long id;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "班级ID")
    private Integer studentId;

    @ApiModelProperty(value = "教师ID")
    private Long teachId;

    @ApiModelProperty(value = "阅读书籍")
    private String bookIds;

    @ApiModelProperty(value = "获取积分")
    private Integer integral;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "开始日期")
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date taskDateStart;

    @ApiModelProperty(value = "截止日期")
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date taskDateEnd;

    @ApiModelProperty(value = "创建者所属机构")
    private Long schoolId;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "是否需要家长反馈")
    private Integer needFk;

    @ApiModelProperty(value = "是否指定图书")
    private Integer appointBook;

    @ApiModelProperty(value = "需发布评论")
    private Integer needPost;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

    @ApiModelProperty(value = "所属活动")
    private Long movementId;

    @ApiModelProperty(value = "班级ID")
    private String classIds;



}
