package org.springcrazy.modules.clazz.pojo;


import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

@Data
public class ClassStudentParamDTO extends PaginateDTO {
    private Long classId;
    private Long lessonId;
    private Boolean unsigned;
}
