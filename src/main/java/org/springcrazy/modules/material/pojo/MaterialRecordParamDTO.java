package org.springcrazy.modules.material.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

@Data
public class MaterialRecordParamDTO extends PaginateDTO {
    private Long studentId;
    private Long staffId;
    private Long materialId;
    private String changeTypeText;
    private Integer changeType;
    private String endDate;
    private String startDate;
}
