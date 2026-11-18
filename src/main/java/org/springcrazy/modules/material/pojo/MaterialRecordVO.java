package org.springcrazy.modules.material.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.material.entity.MaterialRecord;

/**
 * 物料出入库记录
 */
@Data
public class MaterialRecordVO extends MaterialRecord {

    @ApiModelProperty(value = "学生姓名")
    private String studentName;
    @ApiModelProperty(value = "员工姓名")
    private String staffName;
    @ApiModelProperty(value = "物料名")
    private String materialName;

}
