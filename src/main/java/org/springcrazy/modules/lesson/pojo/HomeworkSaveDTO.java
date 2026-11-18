package org.springcrazy.modules.lesson.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class HomeworkSaveDTO extends PaginateDTO {
    private Long id;

    @NotNull(message = "缺少班级")
    private Long classId;

    @NotBlank(message = "缺少标题")
    private String title;

    @NotBlank(message = "缺少作业要求内容")
    private String content;

    private List<String> imgList;
}
