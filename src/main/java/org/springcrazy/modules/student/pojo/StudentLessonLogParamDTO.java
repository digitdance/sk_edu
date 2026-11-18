package org.springcrazy.modules.student.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

/**
 * 课次变动记录记录DTO
 */
@Data
public class StudentLessonLogParamDTO extends PaginateDTO {
    private Long teacherId;
    private Long studentId;
}
