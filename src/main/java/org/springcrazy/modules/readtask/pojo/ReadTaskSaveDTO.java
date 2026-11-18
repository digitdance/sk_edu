package org.springcrazy.modules.readtask.pojo;

import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReadTaskSaveDTO extends PaginateDTO {
    private Long id;

    @NotNull(message = "缺少homeworkId参数")
    private Long taskId;

    private Long studentId;

    @NotNull(message = "缺少内容")
    private String content;

    private List<String> imgList;

}
