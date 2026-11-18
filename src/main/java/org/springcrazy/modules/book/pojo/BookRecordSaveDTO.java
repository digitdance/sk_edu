package org.springcrazy.modules.book.pojo;

import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BookRecordSaveDTO extends PaginateDTO {
    private Long id;

    @NotNull(message = "缺少homeworkId参数")
    private Long homeworkId;

    private Long studentId;

    @NotNull(message = "缺少内容")
    private String content;

    private List<String> imgList;

}
