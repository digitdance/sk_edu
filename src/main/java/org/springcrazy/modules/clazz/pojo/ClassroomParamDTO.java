package org.springcrazy.modules.clazz.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

@Data
public class ClassroomParamDTO extends PaginateDTO {
    private String name;
}
