package org.springcrazy.modules.clazz.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.springcrazy.common.pojo.AutoFillEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 教室
 * </p>
 *
 * @author Ryan
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Classroom对象", description = "教室")
public class Classroom extends AutoFillEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "教室名")
    private String name;

    @ApiModelProperty(value = "位置")
    private String address;

    @ApiModelProperty(value = "面积平方米")
    private Integer area;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;

    @ApiModelProperty(value = "创建者所属学校ID")
    private Long schoolId;
}
