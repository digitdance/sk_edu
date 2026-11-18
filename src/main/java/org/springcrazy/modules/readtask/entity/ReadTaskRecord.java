package org.springcrazy.modules.readtask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.core.mp.base.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 作业提交记录
 * </p>
 *
 * @author Ryan
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "HomeworkRecord对象", description = "作业提交记录")
public class ReadTaskRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "所属作业")
    private Long taskId;

    @ApiModelProperty(value = "图书")
    private Long bookId;

    @ApiModelProperty(value = "读后感")
    private Integer articleId;

    @ApiModelProperty(value = "学生名")
    private Long studentId;

    @ApiModelProperty(value = "提交者")
    private Integer userId;

    @ApiModelProperty(value = "时间")
    private LocalDateTime addTime;

    @ApiModelProperty(value = "内容")
    private String content;
    private String images;
    private Integer score;
    private String comment;
    private LocalDateTime commentTime;
    private Long commentTeacher;
    private String taskCompleteType; //0自助阅读 1读指定书籍 2照相 3发读后感


    @DateTimeFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    @ApiModelProperty("创建时间")
    private Date completeTime;

}
