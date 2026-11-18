package org.springcrazy.modules.readtask.pojo;

import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

import javax.validation.constraints.NotNull;

@Data
public class ReadTaskRecordCommentSaveDTO extends PaginateDTO {
    private Long id;

    @NotNull(message = "缺少homeworkId参数")
    private Long taskId;

    @NotNull(message = "缺少评分")
    private Integer score;

    private String comment;
}
