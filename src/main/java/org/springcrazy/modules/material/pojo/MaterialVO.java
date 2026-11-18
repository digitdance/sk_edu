package org.springcrazy.modules.material.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.material.entity.Material;

/**
 * <p>
 * 物料
 * </p>
 *
 * @author Ryan
 */
@Data
@ApiModel(value = "物料对象", description = "物料")
public class MaterialVO extends Material {

    @ApiModelProperty(value = "所属学校")
    private String schoolName;

    @ApiModelProperty(value = "更新人")
    private String editorName;
}
