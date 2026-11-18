package org.springcrazy.modules.readtask.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReadTaskRecordSaveDTO extends PaginateDTO {
    private Long id;


    @ApiModelProperty(value = "所属作业")
    private Long taskId;

    @ApiModelProperty(value = "图书")
    private Integer bookId;


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
    private Integer taskCompleteType; //读指定书籍 照相 发读后感



    private List<String> imgList;

}
