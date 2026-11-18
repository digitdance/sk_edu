package org.springcrazy.modules.material.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

@Data
public class MaterialParamDTO extends PaginateDTO {
    private String name;
    private String stateText;
    private Integer state;
}
