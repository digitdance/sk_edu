package org.springcrazy.modules.clazz.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

@Data
public class ClassGradeParamDTO extends PaginateDTO {
    private Long id;
    private String name;
    private Integer year;
    private Boolean remark;
}
