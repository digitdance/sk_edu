package org.springcrazy.modules.lesson.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

/**
 * 作业列表查询DTO
 */
@Data
public class HomeworkParamDTO extends PaginateDTO {
    private Long homeworkId;
    private Long teacherId;
    private Long studentId;
    private Long classId;
    private String title;
}
