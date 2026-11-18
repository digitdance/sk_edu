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
public class MovementParamDTO extends PaginateDTO {
    private Long id;

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
