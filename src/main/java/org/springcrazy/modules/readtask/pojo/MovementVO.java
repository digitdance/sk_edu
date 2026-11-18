package org.springcrazy.modules.readtask.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
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
public class MovementVO {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "标题")
    private String logo;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "开始日期")
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date startDate;

    @ApiModelProperty(value = "截止日期")
    @DateTimeFormat(
            pattern = "yyyy-MM-dd"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd"
    )
    private Date endDate;


    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

}
