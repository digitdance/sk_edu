package org.springcrazy.modules.book.pojo;

import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

/**
 * 作业列表查询DTO
 */
@Data
public class BookParamDTO extends PaginateDTO {
    private Long homeworkId;
    private Long teacherId;
    private Integer studentId;
    private Long classId;
    private String title;

    private String keyword;

    private Integer collectType;
    private Integer bookId;
    private Long subjectId;

    private Long taskId;
}
